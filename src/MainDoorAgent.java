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

        // registering the service provided in the yellow pages
        Util.registerService(this, getAID(),"door-status", "HA-door-status");

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

        addBehaviour(new DoorBehaviour(this, 1000));
    }

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
