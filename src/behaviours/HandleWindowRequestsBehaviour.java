package behaviours;

import agents.WindowAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleWindowRequestsBehaviour extends CyclicBehaviour {
    private final WindowAgent agent;

    public HandleWindowRequestsBehaviour(WindowAgent agent){
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
                    if (agent.getWindowState().getValue() != HomeAutomation.WindowStates.BROKEN) {
                        if (messageContent.equals(HomeAutomation.START_SUNFILTER)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.changeWindowState(HomeAutomation.WindowStates.SUNFILTER_ON);
                            response.setContent(agent.getWindowState().getValue().toString());
                        } else if (messageContent.equals(HomeAutomation.STOP_SUNFILTER)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.changeWindowState(HomeAutomation.WindowStates.SUNFILTER_OFF);
                            response.setContent(agent.getWindowState().getValue().toString());
                        } else {
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(HomeAutomation.UNKNOWN_COMMAND);
                        }
                        myAgent.send(response);
                    } else {
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Unable to access the Window because is broken.");
                        myAgent.send(response);
                    }
                    break;
            }
        } else {
            block();
        }
    }
}