import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Class that defines a WindowAgent. This class represents a window of the apartment, and it is responsible
 * for detecting events like intrusions or when is forced
 */
public class WindowAgent extends Agent implements HomeAutomation{

    private WindowStates windowState = WindowStates.CLOSED;

    @Override
    protected void setup() {
        super.setup();

        System.out.println("WindowAgent started...");

        Util.registerService(this,"window-service", "HA-window-service");

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
            ACLMessage windowStateMessage = new ACLMessage(ACLMessage.INFORM);
            windowStateMessage.setContent(windowState.toString());
            windowStateMessage.addReceiver(new AID("Controller", AID.ISLOCALNAME));
            send(windowStateMessage);
        }
    }

    public void changeWindowState(WindowStates windowState){
        this.windowState = windowState;
    }
}
