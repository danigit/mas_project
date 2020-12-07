import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Class that defines the HeatingAgent. This class is responsible for the heating of the apartment.
 * This Agent represents an radiator inside the apartment
 */
public class HeatingAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();

        System.out.println("HeatingAgent starting...");

        // adding behaviour to the agent
        // in this case I added a TickerBehaviour because I assume that the agent
        // interrogates his sensors every 10 seconds to see if the temperature is ok
        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                System.out.println("HeatingAgent gets the temperature...");
            }
        });
    }
}
