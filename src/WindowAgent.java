import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;

import java.util.Random;

/**
 * Class that defines a WindowAgent. This class represents a window of the apartment, and it is responsible
 * for detecting events like intrusions or when is forced
 */
public class WindowAgent extends Agent implements HomeAutomation{

    private WindowStates windowState = WindowStates.BROKEN;

    @Override
    protected void setup() {
        super.setup();

        System.out.println("WindowAgent started...");

        // I can register multiple services at once
        String[] serviceTypes = {"window-service"};
        String[] serviceNames = {"HA-window-service"};

        Util.registerService(this, serviceTypes, serviceNames);

        // adding behaviour to the window
        addBehaviour(new WindowBehaviour(this, 5000));
    }

    /**
     * Class that implements the Window behaviour, controls every
     * x seconds the state of the window and inform the Controller
     * about this state
     */
    private class WindowBehaviour extends TickerBehaviour{

        public WindowBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            // changing the state of the window randomly
            randomWindowState();
            ACLMessage windowStateMessage = new ACLMessage(ACLMessage.INFORM);
            windowStateMessage.setContent(windowState.toString());
            windowStateMessage.setConversationId("window-status");
            windowStateMessage.addReceiver(new AID(ControllerAgent.NAME, AID.ISLOCALNAME));
            send(windowStateMessage);
        }
    }

    public void changeWindowState(WindowStates windowState){
        this.windowState = windowState;
    }

    public void randomWindowState(){
        Random random = new Random();
        if(random.nextInt() % 2 == 0){
            this.windowState = WindowStates.BROKEN;
        }else{
            this.windowState = WindowStates.CLOSED;
        }
    }
}
