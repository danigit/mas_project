package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class GetFridgeList extends Behaviour {
    private AID[] agents;
    private int step, listReceived;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public GetFridgeList(){
        step = 0;
        listReceived = 0;
    }

    public GetFridgeList(AID fridge){
        this();
        this.agents = new AID[]{fridge};
    }

    public GetFridgeList(AID[] fridges){
        this();
        this.agents = fridges;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "fridge-service");

        switch (step){
            // sending the request
            case 0:
                if (result != null && result.length > 0){
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.GET_LIST);
                    message.setConversationId("get-list");

                    for (AID aid : result) {
                        message.addReceiver(aid);
                        Util.log("Sending request for the list to agent " + aid.getLocalName());
                    }

                    myAgent.send(message);
                    listReceived = result.length;
                    step++;
                }else{
                    Util.log("No 'fridge-service' found");
                    step = 2;
                }
                break;
            // getting the answer
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("get-list"));

                if (response != null) {
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the following items have to be purchased: \n" + response.getContent());

                    listReceived--;
                    if (listReceived == 0){
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
    public boolean done() {
        return step == 2;
    }
}