package behaviours;

import agents.WindowAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleWindowRequestsBehaviour extends CyclicBehaviour {
    private final WindowAgent windowAgent;

    public HandleWindowRequestsBehaviour(WindowAgent agent){
        this.windowAgent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();
        if (message != null){
            switch (message.getPerformative()){
                case ACLMessage.REQUEST:
                    ACLMessage response = message.createReply();
                    if (message.getContent().equals(HomeAutomation.START_SUNFILTER)){
                        windowAgent.setWindowState(HomeAutomation.WindowStates.SUNFILTER_ON);
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent(windowAgent.getWindowState().getValue().toString());
                    } else if (message.getContent().equals(HomeAutomation.STOP_SUNFILTER)){
                        windowAgent.setWindowState(HomeAutomation.WindowStates.SUNFILTER_OFF);
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent(windowAgent.getWindowState().getValue().toString());
                    }
                    myAgent.send(response);
            }
        } else {
            block();
        }
    }
}