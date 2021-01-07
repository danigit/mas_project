package behaviours;

import agents.MainDoorAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleDoorRequestsBehaviour extends CyclicBehaviour {
    private final MainDoorAgent agent;

    public HandleDoorRequestsBehaviour(MainDoorAgent agent){
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();

        if (message != null){
            ACLMessage response = message.createReply();
            String messageContent = message.getContent();

            switch (message.getPerformative()){
                // handling the request messages
                case ACLMessage.REQUEST:
                    if (agent.getDoorState().getValue() != HomeAutomation.DoorStates.BROKEN) {
                        if (messageContent.equals(HomeAutomation.GET_STATE)) {
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(agent.getDoorState().getValue().toString());
                        } else if (messageContent.equals(HomeAutomation.CHANGE_STATE)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.changeDoorState(HomeAutomation.DoorStates.valueOf(message.getUserDefinedParameter("new_state")));
                            response.setContent(agent.getDoorState().getValue().toString());
                        } else {
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(HomeAutomation.UNKNOWN_COMMAND);
                        }
                        myAgent.send(response);
                    } else {
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Unable to access the Door because is broken.");
                        myAgent.send(response);
                    }
                    break;
            }
        } else {
            block();
        }
    }
}