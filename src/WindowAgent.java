import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Class that defines a WindowAgent. This class represents a window of the apartment, and it is responsible
 * for detecting events like intrusions or when is forced
 */
public class WindowAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();

        System.out.println("WindowAgent started...");

        // adding behaviour to the window
        // has pretty much the same behaviour as the door
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("WindowAgent sending action...");
            }
        });
    }
}
