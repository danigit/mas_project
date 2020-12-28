package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class GetFridgeList extends Behaviour {
    private AID[] fridges;
    private int step, listReceived;

    public GetFridgeList(){
        step = 0;
        listReceived = 0;
    }

    public GetFridgeList(AID fridge){
        this.fridges = new AID[]{fridge};
        step = 0;
        listReceived = 0;
    }

    public GetFridgeList(AID[] fridges){
        this.fridges = fridges;
        step = 0;
        listReceived = 0;
    }

    @Override
    public void action() {
        AID[] result;

        if (fridges.length > 0) {
            result = fridges;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "fridge-service");
            if (descriptions != null) {
                result = Util.getAIDFromDescriptions(descriptions);
            } else{
                result = null;
            }
        }

        switch (step){
            case 0:
                if (result != null && result.length > 0){
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.GET_LIST);
                    message.setConversationId("get-list");

                    for (int i = 0; i < result.length; i++){
                        message.addReceiver(result[i]);
                        Util.log("Sending request for the list to agent " + (result[i]).getLocalName());
                    }

                    myAgent.send(message);
                    listReceived = result.length;
                    step++;
                }else{
                    Util.log("No 'fridge-service' found");
                    step = 2;
                }
                break;
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
        }
    }

    @Override
    public boolean done() {
        return step == 2;
    }
}