package behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import interfaces.HomeAutomation;
import utils.Util;

public class CloseShutter extends Behaviour {
    private AID[] shutters;
    private int step, responses;

    public CloseShutter(){
        step = 0;
        responses = 0;
    }

    public CloseShutter(AID agent){
        this();
        this.shutters = new AID[]{agent};
    }

    public CloseShutter(AID[] agents){
        this();
        this.shutters = agents;
    }

    @Override
    public void action() {
        AID[] result;

        if (shutters.length > 0) {
            result = shutters;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "shutter-service");
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
                    message.setContent(HomeAutomation.DOWN_SHUTTER);
                    message.setConversationId("down-shutter");
                    for (AID agent : result) {
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
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("down-shutter"));
                if (response != null) {
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the shutter in down");
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
        }
    }

    @Override
    public boolean done(){
        return step == 2;
    }
}