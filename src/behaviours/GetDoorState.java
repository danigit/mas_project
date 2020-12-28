package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import utils.Util;

public class GetDoorState extends Behaviour {
    private AID[] doors;
    private int step, responses;

    public GetDoorState(){
        step = 0;
        responses = 0;
    }

    public GetDoorState(AID door) {
        this.doors = new AID[]{door};
        step = 0;
        responses = 0;
    }

    public GetDoorState(AID[] doors){
       this.doors = doors;
        step = 0;
        responses = 0;
    }

    @Override
    public void action() {
        AID[] result;

        if (doors.length > 0) {
            result = doors;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "door-service");
            if (descriptions != null) {
                result = Util.getAIDFromDescriptions(descriptions);
            } else{
                result = null;
            }
        }

        switch (step) {
            case 0:
                if (result != null && result.length > 0) {
                    responses = result.length;

                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.STATE);
                    message.setConversationId("door-status");

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
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("door-status"));

                if (response != null){
                    Util.log("Agent " + response.getSender().getLocalName() + " has informed the Controller "+
                            "that it is in state: " + response.getContent());
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
    public boolean done() {
        return step == 2;
    }
}
