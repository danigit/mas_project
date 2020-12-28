package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class StartHeatBehaviour extends Behaviour {
    private AID[] heats;
    private int step, responses;

    public StartHeatBehaviour() {
        step = 0;
        responses = 0;
    }

    public StartHeatBehaviour(AID agent){
        this.heats = new AID[]{agent};
        step = 0;
        responses = 0;
    }

    public StartHeatBehaviour(AID[] agents){
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
                if (result != null && result.length > 0) {
                    responses = result.length;
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.HeatStates.START.toString());
                    message.setConversationId("start-heat");

                    for (AID agent : result) {
                        message.addReceiver(agent);
                        Util.log("Asking to the agent " + agent.getLocalName() + " to start go in start state");
                    }
                    myAgent.send(message);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No door-service found!");
                    step = 2;
                }
                break;
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("start-heat"));

                if (response != null){
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the heat is on");
                    Util.log("Informing the User that the " + response.getSender().getLocalName() + " is in state: "+
                            response.getContent());

                    responses--;
                    if (responses == 0){
                        step++;
                    }
                } else{
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