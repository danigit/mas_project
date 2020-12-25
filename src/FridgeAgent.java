import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Class that defines the FridgeAgent. This agent is responsible for informing the ControllerAgent what is the
 * food situation in apartment
 */
public class FridgeAgent extends Agent implements HomeAutomation{

    private FridgeStates fridgeState = FridgeStates.RUNNING;

    @Override
    protected void setup() {
        super.setup();

        System.out.println("FridgeAgent starting...");

        // I can register multiple services at once
        String[] serviceTypes = {"fridge-service"};
        String[] serviceNames = {"HA-fridge-service"};

        Util.registerService(this,serviceTypes, serviceNames);

        // adding behaviour to the window
        addBehaviour(new FridgeBehaviour(this, 5000));
    }

    /**
     * Class that implements the Fridge behaviour, it sends
     * every x seconds a message to the Controller to inform him
     * about the state of the fridge
     *
     * I choose a TickerBehaviour here because if the fridge is
     * broken the user must be informed because the goods inside
     * go bad
     */
    private class FridgeBehaviour extends TickerBehaviour {

        public FridgeBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            // sending the message only if the fridge is broken
            if (fridgeState == FridgeStates.BROKEN) {
                ACLMessage fridgeStateMessage = new ACLMessage(ACLMessage.INFORM);
                fridgeStateMessage.setContent(fridgeState.toString());
                fridgeStateMessage.setConversationId("fridge-status");
                fridgeStateMessage.addReceiver(new AID(ControllerAgent.NAME, AID.ISLOCALNAME));
                send(fridgeStateMessage);
            }
        }
    }

    public void changeFridgeState(HomeAutomation.FridgeStates fridgeState){
        this.fridgeState = fridgeState;
    }
}
