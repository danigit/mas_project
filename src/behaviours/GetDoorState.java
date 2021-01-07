package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import utils.Util;

public class GetDoorState extends Behaviour {
    private AID[] agents;
    private int step, responses;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public GetDoorState(){
        step = 0;
        responses = 0;
    }

    public GetDoorState(AID door) {
        this();
        this.agents = new AID[]{door};
    }

    public GetDoorState(AID[] doors){
        this();
        this.agents = doors;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "door-service");

        switch (step) {
            // sending the request
            case 0:
                if (result != null && result.length > 0) {
                    responses = result.length;

                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.GET_STATE);
                    message.setConversationId("get-door-state");

                    for (AID agent : result) {
                        message.addReceiver(agent);
                        Util.log("Asking to the agent " + agent.getLocalName() + " in which state is it");
                    }
                    myAgent.send(message);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No door-service found!");
                    step = 2;
                }
                break;
            // getting the result
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("get-door-state"));

                if (response != null){
                    switch (response.getPerformative()){
                        case ACLMessage.INFORM:
                            Util.log("Agent " + response.getSender().getLocalName() + " has informed the Controller "+
                                    "that it is in state: " + response.getContent());
                            Util.log("Informing the User that the " + response.getSender().getLocalName() + " is in state: "+
                                    response.getContent());
                            break;
                        case ACLMessage.FAILURE:
                            Util.log("Agent " + response.getSender().getLocalName() + " has sent the following message "+
                                    "to the ControllerAgent: " + response.getContent());
                            Util.log("Informing the User that the the agent " + response.getSender().getLocalName() +
                                    " is broken.");
                            break;
                    }

                    // controlling that all the agents have answered
                    responses--;
                    if (responses == 0){
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
