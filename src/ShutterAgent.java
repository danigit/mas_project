import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Class that defines the ShutterAgent. This class is responsible for lowering/raising the shutter. This
 * event could happen at a precise hour or may be triggered by some other agent
 */
public class ShutterAgent extends Agent implements HomeAutomation{

    private ShutterStates shutterState = ShutterStates.BROKEN;

    @Override
    protected void setup() {
        super.setup();

        System.out.println("ShutterAgent starting...");

        String[] serviceTypes = {"shutter-service"};
        String[] serviceNames = {"HA-shutter-service"};

        Util.registerService(this, serviceTypes, serviceNames);

        // adding behaviour to the agent
        addBehaviour(new ShutterBehaviour(this, 5000));
    }

    /**
     * Class that implements the behaviour of the Shutter agent,
     * it controls every x seconds the state of the shutter, and
     * communicates it to the Controller agent
     */
    private class ShutterBehaviour extends TickerBehaviour{

        public ShutterBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            ACLMessage shutterStateMessage = new ACLMessage(ACLMessage.INFORM);
            shutterStateMessage.setContent(shutterState.toString());
            shutterStateMessage.addReceiver(new AID(ControllerAgent.NAME, AID.ISLOCALNAME));
            send(shutterStateMessage);
        }
    }

    public void changeShutterState(ShutterStates shutterState){
        this.shutterState = shutterState;
    }
}
