package behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import interfaces.HomeAutomation;
import utils.Util;

public class StopSunFilter extends Behaviour {
    private AID[] windows;
    private int step, responses;

    public StopSunFilter(){
        step = 0;
        responses = 0;
    }

    public StopSunFilter(AID agent){
        this.windows = new AID[]{agent};
        step = 0;
        responses = 0;
    }

    public StopSunFilter(AID[] agents) {
        this.windows = agents;
        step = 0;
        responses = 0;
    }

    @Override
    public void action() {
        AID[] result;

        if (windows.length > 0) {
            result = windows;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "window-service");
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
                    message.setContent(HomeAutomation.STOP_SUNFILTER);
                    message.setConversationId("stop-sunfilter");
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
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("stop-sunfilter"));
                if (response != null) {
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the sun filter in off");
                    Util.log("Informing the user that the agent " + response.getSender().getLocalName() + " that the "+
                            "the sun filters are in state: " + response.getContent());

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