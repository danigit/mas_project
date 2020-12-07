import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Class that defines the MainDoorAgent. This is the main door of the apartment, so it is responsible for reporting
 * intrusions or other events like this to the ControllerAgent
 */
public class MainDoorAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();

        System.out.println("MainDoorAgent starting...");

        // adding behaviour to the agent
        // for now I define a OneShotBehaviour because the door has to send events only when some
        // internal state has changed
        // another way of modeling this could be that the agent continuously interrogates the sensors
        // to see their status. In this case we have to use a TickerBehaviour
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("MainDoorAgent sending action...");
            }
        });
    }
}
