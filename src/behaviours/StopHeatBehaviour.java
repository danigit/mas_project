package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class StopHeatBehaviour extends Behaviour {
    private AID[] heats;
    private int step, responses;

    public StopHeatBehaviour(){
        step = 0;
        responses = 0;
    }

    public StopHeatBehaviour(AID agent){
        this.heats = new AID[]{agent};
        step = 0;
        responses = 0;
    }

    public StopHeatBehaviour(AID[] agents){
        this.heats = agents;
        step = 0;
        responses = 0;
    }

    @Override
    public void action() {
        AID[] result;

        if (heats.length > 0) {
            result = heats;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "heat-service");
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
                    message.setContent(HomeAutomation.HeatStates.STOP.toString());
                    message.setConversationId("stop-heat");

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
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("stop-heat"));
                if (response != null) {
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the heat is off");
                    Util.log("Informing the User that the agent " + response.getSender().getLocalName() + " is in state "+
                            response.getContent());
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