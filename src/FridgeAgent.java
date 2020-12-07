import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Class that defines the FridgeAgent. This agent is responsible for informing the ControllerAgent what is the
 * food situation in apartment
 */
public class FridgeAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();

        System.out.println("FridgeAgent starting...");

        // adding behaviour to the agent
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("FridgeAgent making an action...");
            }
        });
    }
}
