package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import utils.*;

public class GetHeatTemperatureBehaviour extends Behaviour {
    AID[] agents;
    int step, responses;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public GetHeatTemperatureBehaviour(){
        step = 0;
        responses = 0;
    }

    public GetHeatTemperatureBehaviour(AID agent) {
        this();
        this.agents = new AID[]{agent};
    }

    public GetHeatTemperatureBehaviour(AID[] heatAgents){
        this();
        this.agents = heatAgents;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "heat-service");

        switch (step) {
            // sending the request
            case 0:
                if (result != null && result.length > 0) {
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.GET_TEMPERATURE);
                    message.setConversationId("get-temp-value");

                    for (AID agent : result) {
                        message.addReceiver(agent);
                        Util.log("Sending request for temperature to the agent " + agent.getLocalName());
                    }

                    myAgent.send(message);
                    responses = result.length;
                    step++;
                } else{
                    Util.log("No 'heat-service' found!");
                    step = 2;
                }
                break;
            // getting the answers
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("get-temp-value"));
                if (response != null){
                    switch (response.getPerformative()) {
                        case ACLMessage.INFORM:
                            Util.log("The agent " + response.getSender().getLocalName() + " has informed the controller that " +
                                    "the temperature is: " + response.getContent());
                            break;
                        case ACLMessage.FAILURE:
                            Util.log("Agent " + response.getSender().getLocalName() + " has sent the following message "+
                                    "to the ControllerAgent: " + response.getContent());
                            Util.log("Informing the User that the the agent " + response.getSender().getLocalName() +
                                    " is broken");
                            break;
                    }

                    // controlling that all the agents have answered
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