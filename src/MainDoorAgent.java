import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 * Class that defines the MainDoorAgent. This is the main door of the apartment, so it is responsible for reporting
 * intrusions or other events like this to the ControllerAgent
 */
public class MainDoorAgent extends Agent implements HomeAutomation{

    private DoorStates doorState = DoorStates.BROKEN;

    @Override
    protected void setup() {
        super.setup();

        System.out.println("MainDoorAgent starting...");

        // registering the services provided in the yellow pages
        Util.registerService(this,"door-service", "HA-door-service");

        // adding behaviour to the agent
        addBehaviour(new DoorBehaviour(this, 1000));
    }

    /**
     * Class that implements the MainDoor behaviour, it sends
     * every x seconds a message to the Controller to inform him
     * about the state of the door
     */
    private class DoorBehaviour extends TickerBehaviour{

        public DoorBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            ACLMessage doorLockedMessage = new ACLMessage(ACLMessage.INFORM);
            doorLockedMessage.setContent(doorState.toString());
            doorLockedMessage.addReceiver(new AID("Controller", AID.ISLOCALNAME));
            send(doorLockedMessage);
        }
    }

    public void changeDoorState(DoorStates doorState){
        this.doorState = doorState;
    }
}
