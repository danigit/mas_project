import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Class that defines the RoomAgent. This agent represent a room in the apartment and it can contain inside it
 * other agents as Light, Window, Heating
 */
public class RoomAgent extends Agent{

    @Override
    protected void setup() {
        super.setup();

        System.out.println("RoomAgent started...");

        // adding the behaviour to the agent
        // here I have to decide if the elements in the room are handled through the room agent or
        // through ControllerAgent. In the firs case I have to use an Cyclic or Ticker behaviour,
        // since the RoomAgent has to control the state of the agents inside it, in the second case
        // the RoomAgent handles only his messages, so a OneShotBehaviour could be enough
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("RoomAgent sending action...");
            }
        });
    }
}
