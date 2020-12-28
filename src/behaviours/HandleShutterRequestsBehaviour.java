package behaviours;

import agents.ShutterAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleShutterRequestsBehaviour extends CyclicBehaviour {
    private final ShutterAgent shutterAgent;

    public HandleShutterRequestsBehaviour(ShutterAgent agent){
        this.shutterAgent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();
        if (message != null){
            switch (message.getPerformative()){
                case ACLMessage.REQUEST:
                    ACLMessage response = message.createReply();
                    response.setPerformative(ACLMessage.INFORM);
                    if (message.getContent().equals(HomeAutomation.UP_SHUTTER)){
                        shutterAgent.setShutterState(HomeAutomation.ShutterStates.UP);
                        response.setContent(shutterAgent.getShutterState().getValue().toString());
                    } else if(message.getContent().equals(HomeAutomation.DOWN_SHUTTER)){
                        shutterAgent.setShutterState(HomeAutomation.ShutterStates.DOWN);
                    }
                    myAgent.send(response);
                    break;
            }
        } else {
            block();
        }
    }
}