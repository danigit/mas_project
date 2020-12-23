import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;
import jade.util.Logger;
import jade.util.leap.Iterator;

import java.util.Vector;


/**
 * Class that defines the main agent. This class has the objective to coordinate and make requests to the other
 * agents
 */
public class ControllerAgent extends Agent implements HomeAutomation{

    static final String NAME = "ControllerAgent";

    @Override
    protected void setup() {
        super.setup();

        Util.logger = Logger.getMyLogger(getLocalName());

        Util.log("ControllerAgent has started...");
        Util.logger.log(Logger.INFO, "ControllerAgent has started...");

        // creating the subscription message to the DFService for a service
        ACLMessage dfSubscriptionMessage = Util.subscribeToService(this, getDefaultDF(), "control-service");

        // adding behaviours to the agent
        addBehaviour(new DFSubscriptionBehaviour(this, dfSubscriptionMessage));
    }

    /**
     * Class that makes a subscription to the DFAgent for a given service
     */
    private class DFSubscriptionBehaviour extends SubscriptionInitiator{
        ACLMessage subscriptionMessage = new ACLMessage(ACLMessage.SUBSCRIBE);

        public DFSubscriptionBehaviour(Agent agent, ACLMessage subscriptionMessage){
            super(agent, subscriptionMessage);
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] result = DFService.decodeNotification(inform.getContent());
                if (result.length > 0){
                    for (int i = 0; i < result.length; i++) {
                        // making the subscription to the founded agents
                        addBehaviour(new SubscriptionController(myAgent, subscriptionMessage, result));
                    }
                }
            } catch (FIPAException fipaException) {
                fipaException.printStackTrace();
                Util.logger.log(Logger.SEVERE, "Cannot make subscription to DFService! See stack trace for more information!");
                Util.log("Cannot make subscription to DFService! See stack trace for more information!");
            }
        }
    }

    /**
     * Class that makes a subscription agents that provide a certain service
     */
    private class SubscriptionController extends SubscriptionInitiator{
        AID[] controllerAgents;
        Agent agent;
        ACLMessage message;
        DFAgentDescription[] serviceAgents;
        Vector<ACLMessage> subscriptions = new Vector<>();

        public SubscriptionController(Agent agent, ACLMessage message, DFAgentDescription[] serviceAgents) {
            super(agent, message);
            this.agent = agent;
            this.message = message;
            this.serviceAgents = serviceAgents;
        }

        @Override
        protected Vector prepareSubscriptions(ACLMessage subscription) {
            subscription.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);

            if (serviceAgents != null && serviceAgents.length > 0) {
                controllerAgents = new AID[serviceAgents.length];
                for (int i = 0; i < serviceAgents.length; i++) {
                    controllerAgents[i] = serviceAgents[i].getName();
                    subscription.addReceiver(serviceAgents[i].getName());
                }
            }

            subscription.setContent(BROKEN);
            subscriptions.addElement(subscription);
            return subscriptions;
        }

        @Override
        protected void handleAgree(ACLMessage agree) {
            Util.logger.log(Logger.INFO, agree.getSender().getLocalName() + " has accepted the subscription request");
            Util.log( agree.getSender().getLocalName() + " has accepted the subscription request");
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            Util.logger.log(Logger.INFO, "Received subscription inform from " + inform.getSender().getLocalName()
            + " with the following content: '" + inform.getContent() + "'");

            Util.log("Received subscription inform from " + inform.getSender().getLocalName()
            + " with the following content: '" + inform.getContent() + "'");

            if (inform.getContent().equals(BROKEN)) {
                Util.log("Informing person that " + inform.getSender().getLocalName() + " is broken!");
            }
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            Util.logger.log(Logger.INFO, "Received subscription refuse from " + refuse.getSender().getLocalName()
                    + " with the following content: '" + refuse.getContent() + "'");

            Util.log("Received subscription inform from " + refuse.getSender().getLocalName()
                    + " with the following content: '" + refuse.getContent() + "'");
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
