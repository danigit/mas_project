import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Class that defines the ShutterAgent. This class is responsible for lowering/raising the shutter. This
 * event could happen at a precise hour or may be triggered by some other agent
 */
public class ShutterAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();

        System.out.println("ShutterAgent starting...");

        // adding behaviour to the agent
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("ShutterAgent sending an action...");
            }
        });
    }
}
