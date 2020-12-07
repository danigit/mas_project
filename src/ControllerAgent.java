import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;


/**
 * Class that defines the main agent. This class has the objective to coordinate and make requests to the other
 * agents
 */
public class ControllerAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Starting the master Agent...");

        // adding behaviour to this agent
        // I choose a TickerBehaviour because the agent has to control periodically the status
        // of the other agents and sent messages to them
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                System.out.println("Controlling the agents status and making requests...");
            }
        });
    }
}
