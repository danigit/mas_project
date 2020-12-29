package behaviours;

import agents.ShutterAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleShutterRequestsBehaviour extends CyclicBehaviour {
    private final ShutterAgent agent;

    public HandleShutterRequestsBehaviour(ShutterAgent agent){
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();

        if (message != null){
            ACLMessage response = message.createReply();
            String messageContent = message.getContent();

            switch (message.getPerformative()){
                case ACLMessage.REQUEST:
                    if (agent.getShutterState().getValue() != HomeAutomation.ShutterStates.BROKEN) {
                        if (messageContent.equals(HomeAutomation.UP_SHUTTER)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.setShutterState(HomeAutomation.ShutterStates.UP);
                            response.setContent(agent.getShutterState().getValue().toString());
                        } else if (messageContent.equals(HomeAutomation.DOWN_SHUTTER)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.setShutterState(HomeAutomation.ShutterStates.DOWN);
                            response.setContent(agent.getShutterState().getValue().toString());
                        } else {
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(HomeAutomation.UNKNOWN_COMMAND);
                        }
                    } else{
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Unable to access the Shutter because is broken.");
                    }
                    myAgent.send(response);
                    break;
            }
        } else {
            block();
        }
    }
}