package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class SetHeatTemperatureBehaviour extends Behaviour {
    private AID[] agents;
    private int step, responses;
    private double temperature;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public SetHeatTemperatureBehaviour(double temperature){
        step = 0;
        responses = 0;
        this.temperature = temperature;
    }
    public SetHeatTemperatureBehaviour(AID agent, double temperature) {
        this(temperature);
        this.agents = new AID[]{agent};
    }

    public SetHeatTemperatureBehaviour(AID[] agents, double temperature){
        this(temperature);
        this.agents = agents;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "door-service");

        switch (step) {
            case 0:
                if (result != null && result.length > 0) {
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.SET_TEMPERATURE);
                    message.addUserDefinedParameter("temp", String.valueOf(this.temperature));
                    message.setConversationId("temp-set");

                    for (AID receiver : result) {
                        message.addReceiver(receiver);
                        Util.log("Asking to agent " + receiver.getLocalName() + " to set the following temperature: " +
                                this.temperature);
                    }

                    myAgent.send(message);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No heat-service found!");
                    step = 2;
                }
                break;
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("temp-set"));

                if (response != null){
                    Util.log( "The agent " + response.getSender().getLocalName() + " has communicated to the Controller "+
                            "that the temperature was set at: " + response.getContent());
                    Util.log("Communicating to the User tha the agent " + response.getSender().getLocalName() + " has set " +
                            "the temperature at: " + response.getContent());
                    responses--;

                    if (responses == 0) {
                        step++;
                    }
                } else{
                    block();
                }
                break;
            default: step = 2;
        }
    }

    @Override
    public boolean done() {
        return step == 2;
    }
}
