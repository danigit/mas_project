import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.DummyAgent.DummyAgent;
import jade.util.leap.Iterator;

import javax.print.attribute.standard.Finishings;
import java.awt.image.RasterOp;


/**
 * Class that defines the main agent. This class has the objective to coordinate and make requests to the other
 * agents
 */
public class ControllerAgent extends Agent implements HomeAutomation{

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Starting the master Agent...");

        // adding a behaviour to the agent
        // this is a cyclic behaviour that controls the incoming messages
        // and act upon them
        addBehaviour(new ControllerBehaviour());
    }

    /**
     * Class that implements a behaviour that controls the incoming
     * messages and act upon them
     */
    private class ControllerBehaviour extends CyclicBehaviour{

        @Override
        public void action() {

            // getting all the messages of type inform
            ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            if (message != null){
                String messageContent = message.getContent();
                // controlling who sent the message
                if (messageContent.equals(DoorStates.BROKEN.toString())){
                   Util.log("Someone entered the house without permission");
                }
            }else {
                block();
            }
        }
    }

    /**
     * Class that implements the behaviour which ask to the Heat
     * agents to communicate their temperature
     */
    private class GetTemperatureBehaviour extends Behaviour{
        AID[] heatAgents;
        int step = 0, tempReceived = 0;
        double temperature;

        @Override
        public void action() {
            switch (step) {
                case 0:
                    Util.log("Sending temp request");
                    DFAgentDescription[] result = Util.searchDFTemplate(myAgent, "heat-service");

                    if (result != null && result.length > 0) {
                        ACLMessage intentionMessage = new ACLMessage(ACLMessage.REQUEST);
                        intentionMessage.setContent(HeatAgent.GET_TEMPERATURE.toString());

                        heatAgents = new AID[result.length];
                        for (int i = 0; i < result.length; i++) {
                            heatAgents[i] = result[i].getName();
                            intentionMessage.addReceiver(result[i].getName());
                        }
                        myAgent.send(intentionMessage);
                        step++;
                    } else{
                        Util.log("No 'heat-service' found!");
                        step = 2;
                    }
                    break;
                case 1:
                    ACLMessage response = receive(MessageTemplate.MatchConversationId("temp-value"));
                    if (response != null){
                        temperature = Double.parseDouble(response.getContent());

                        Util.log("The temperature from " + response.getSender().getName() + " is: " + response.getContent());
                        tempReceived++;

                        if (tempReceived >= heatAgents.length) {
                            step++;
                        }
                    } else{
                        block();
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            return step == 2;
        }
    }

    /**
     * Class that implement the behaviour which ask to the Heat
     * Agents to set a particular temperature
     */
    private class SetTemperatureBehaviour extends Behaviour{

        private int step = 0, tempSet = 0;
        private double temperature;
        private AID[] receivers;

        public SetTemperatureBehaviour(AID receiver, double temperature) {
            receivers = new AID[1];
            receivers[0] = receiver;
            this.temperature = temperature;
        }

        public SetTemperatureBehaviour(AID[] receivers, double temperature){
            this.receivers = receivers;
            this.temperature = temperature;
        }

        @Override
        public void action() {
            DFAgentDescription[] result = Util.searchDFTemplate(myAgent, "heat-service");

            switch (step) {
                case 0:
                    if (result != null && result.length > 0) {
                        ACLMessage intentionMessage = new ACLMessage(ACLMessage.REQUEST);
                        intentionMessage.setContent(HeatAgent.SET_TEMPERATURE.toString());
                        intentionMessage.addUserDefinedParameter("temp", String.valueOf(this.temperature));

                        Util.log("Sending set message");
                        for (AID receiver : receivers) {
                            intentionMessage.addReceiver(receiver);
                        }

                        myAgent.send(intentionMessage);
                        step++;
                    }else{
                        Util.log("No heat-service found!");
                        step = 2;
                    }
                    break;
                case 1:
                    ACLMessage response = receive(MessageTemplate.and(
                            MessageTemplate.MatchConversationId("temp-set"),
                            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM))
                    );

                    if (response != null){
                        Util.log("Temperature set on agent: " + response.getSender().getName() + " at: " + response.getContent());

                        tempSet--;

                        if (tempSet == 0) {
                            step++;
                        }
                    } else{
                        block();
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            return step == 2;
        }
    }

    public void getTemperature(){
        this.addBehaviour(new GetTemperatureBehaviour());
    }

    public void setTemperature(AID[] agents, double temperature){
        this.addBehaviour(new SetTemperatureBehaviour(agents, temperature));
    }
}
