import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * Class that defines the main agent. This class has the objective to coordinate and make requests to the other
 * agents
 */
public class ControllerAgent extends Agent implements HomeAutomation{

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Starting the master Agent...");

        // adding a behaviour to the agent that periodically
        // gets messages from all the agents
        addBehaviour(new ControllerBehaviour());
    }

    private class ControllerBehaviour extends CyclicBehaviour{

        @Override
        public void action() {

            ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            if (message != null){
                String messageContent = message.getContent();
                if (messageContent.equals(DoorStates.BROKEN.toString())){
                   log("Someone entered the house without permission");
                }
            }else {
                block();
            }
        }
    }

    public void log(String text){
        System.out.println(text);
    }
}
