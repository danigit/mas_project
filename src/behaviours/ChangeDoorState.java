package behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import interfaces.HomeAutomation;
import utils.Util;

public class ChangeDoorState extends Behaviour {
    AID[] doors;
    HomeAutomation.DoorStates doorStatus;
    int step, responses;

    public ChangeDoorState(){
        step = 0;
        responses = 0;
    }

    public ChangeDoorState(AID agent, HomeAutomation.DoorStates doorStatus){
        this.doors = new AID[]{agent};
        this.doorStatus = doorStatus;
        step = 0;
        responses = 0;
    }

    public ChangeDoorState(AID[] agents, HomeAutomation.DoorStates doorStatus){
        this.doors = agents;
        this.doorStatus = doorStatus;
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
                if (result != null && result.length > 0){
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.CHANGE_STATE);
                    message.addUserDefinedParameter("new_state", doorStatus.toString());
                    message.setConversationId("change-door-state");

                    for (AID agent : result){
                        message.addReceiver(agent);
                        Util.log("Asking to the agent " + agent.getLocalName() + " to change his state in: "+
                                doorStatus.toString());
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
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("change-door-state"));

                if (response != null) {
                    if( response.getPerformative() == ACLMessage.INFORM) {
                        Util.log("Agent " + response.getSender().getLocalName() + " has informed the Controller that he changed his state to: "+
                                response.getContent());
                        Util.log("Informing the User that the agent " + response.getSender().getLocalName()+
                                " is now in state " + response.getContent());
                    } else if (response.getPerformative() == ACLMessage.FAILURE){
                        Util.log("Agent has informed the Controller that he was not able to change his state "+
                                "because is in state broken");
                        Util.log("Informing the User that the agent " + response.getSender().getLocalName()+
                                " could not change his state");
                    }
                    responses--;
                    if (responses == 0){
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