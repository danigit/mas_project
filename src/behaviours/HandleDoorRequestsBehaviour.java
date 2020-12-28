package behaviours;

import agents.MainDoorAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import sun.util.resources.cldr.mua.CurrencyNames_mua;

public class HandleDoorRequestsBehaviour extends CyclicBehaviour {
    private MainDoorAgent doorAgent;

    public HandleDoorRequestsBehaviour(MainDoorAgent agent){
        this.doorAgent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();
        if (message != null){
            switch (message.getPerformative()){
                case ACLMessage.REQUEST:
                    ACLMessage response = message.createReply();
                    if (message.getContent().equals(HomeAutomation.STATE)) {
                        response.setPerformative(ACLMessage.INFORM);
                        response.setConversationId("door-status");
                        response.setContent(doorAgent.getDoorState().getValue().toString());
                    } else if(message.getContent().equals(HomeAutomation.CHANGE_STATE)){
                        if (doorAgent.getDoorState().getValue() == HomeAutomation.DoorStates.BROKEN){
                            response.setPerformative(ACLMessage.FAILURE);
                        } else {
                            doorAgent.setDoorState(HomeAutomation.DoorStates.valueOf(message.getUserDefinedParameter("new_state")));
                            response.setPerformative(ACLMessage.INFORM);
                        }
                        response.setContent(doorAgent.getDoorState().getValue().toString());
                    }
                    myAgent.send(response);
                    break;
            }
        } else {
            block();
        }
    }
}