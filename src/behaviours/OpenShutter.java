package behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import interfaces.HomeAutomation;
import utils.Util;

public class OpenShutter extends Behaviour {
    private AID[] agents;
    private int step, responses;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public OpenShutter(){
        step = 0;
        responses = 0;
    }

    public OpenShutter(AID agent){
        this();
        this.agents = new AID[]{agent};
    }

    public OpenShutter(AID[] agents){
        this();
        this.agents = agents;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "shutter-service");

        switch (step){
            // sending request
            case 0:
                if (result != null && result.length > 0){
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.UP_SHUTTER);
                    message.setConversationId("up-shutter");
                    for (AID agent : result) {
                        Util.log("Asking the agent " + agent.getLocalName() + " to close the shutter");
                        message.addReceiver(agent);
                    }
                    myAgent.send(message);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No 'fridge-service' found");
                    step = 2;
                }
                break;
            // getting the response
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("up-shutter"));
                if (response != null) {
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the shutter in up");
                    Util.log("Informing the User that the agent " + response.getSender().getLocalName() + " has the shutter "+
                            "in state: " + response.getContent());

                    responses--;
                    if (responses == 0) {
                        step++;
                    }
                } else {
                    block();
                }
                break;
            default: step = 2;
        }
    }

    @Override
    public boolean done(){
        return step == 2;
    }
}