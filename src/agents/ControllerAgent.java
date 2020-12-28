package agents;

import behaviours.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;
import jade.util.Logger;

import java.util.Vector;

import interfaces.*;
import utils.Util;

/**
 * Class that defines the main agent. This class has the objective to coordinate and make requests to the other
 * agents
 */
public class ControllerAgent extends Agent implements HomeAutomation, Controller{

    public static final String NAME = "ControllerAgent";

    public ControllerAgent(){
        registerO2AInterface(Controller.class, this);
    }

    @Override
    protected void setup() {
        super.setup();

        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("ControllerAgent has started...");
        Util.logger.log(Logger.INFO, "ControllerAgent has started...");

        // creating the subscription message to the DFService for a service
        ACLMessage dfSubscriptionMessage = Util.subscribeToService(this, getDefaultDF(), "control-service");

        // adding behaviours to the agent

        // this is the behaviour that subscribes to the DF for the 'control-service'
        addBehaviour(new DFSubscriptionBehaviour(this, dfSubscriptionMessage));

        // this is the behaviour that handles the incoming messages from the agents
        addBehaviour(new HandleRequests());
    }

    /**
     * Class that makes a subscription to the DFAgent for a given service
     */
    private class DFSubscriptionBehaviour extends SubscriptionInitiator{
        ACLMessage subscriptionMessage = new ACLMessage(ACLMessage.SUBSCRIBE);

        public DFSubscriptionBehaviour(Agent agent, ACLMessage subscriptionMessage){
            super(agent, subscriptionMessage);
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] result = DFService.decodeNotification(inform.getContent());
                if (result.length > 0){
                    for (int i = 0; i < result.length; i++) {
                        // making the subscription to the founded agents
                        addBehaviour(new SubscriptionController(myAgent, subscriptionMessage, result));
                    }
                }
            } catch (FIPAException fipaException) {
                fipaException.printStackTrace();
                Util.log("Cannot make subscription to DFService! See stack trace for more information!");
            }
        }
    }

    /**
     * Class that makes a subscription agents that provide a given service
     */
    private static class SubscriptionController extends SubscriptionInitiator{
        AID[] controllerAgents;
        ACLMessage message;
        DFAgentDescription[] serviceAgents;
        Vector<ACLMessage> subscriptions = new Vector<>();

        public SubscriptionController(Agent agent, ACLMessage message, DFAgentDescription[] serviceAgents) {
            super(agent, message);
            this.message = message;
            this.serviceAgents = serviceAgents;
        }

        @Override
        protected Vector prepareSubscriptions(ACLMessage subscription) {
            subscription.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);

            if (serviceAgents != null && serviceAgents.length > 0) {
                controllerAgents = new AID[serviceAgents.length];
                for (int i = 0; i < serviceAgents.length; i++) {
                    controllerAgents[i] = serviceAgents[i].getName();
                    subscription.addReceiver(serviceAgents[i].getName());
                }
            }

            // subscribing for broken status
            subscription.setContent(BROKEN);
            subscriptions.addElement(subscription);
            return subscriptions;
        }

        @Override
        protected void handleAgree(ACLMessage agree) {
            Util.log( agree.getSender().getLocalName() + " has accepted the subscription request");
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            // getting the subscription agree messages
            if (inform.getPerformative() == ACLMessage.AGREE) {
                Util.log("Received subscription inform from " + inform.getSender().getLocalName()
                        + " with the following content: '" + inform.getContent() + "'");
            }
            // getting the messages triggered by the subscription
            else if (inform.getPerformative() == ACLMessage.INFORM) {
                Util.log("Agent " + inform.getSender().getLocalName() + " has informed the Controller with the following  " +
                        "content: " + inform.getContent());
                if (inform.getContent().equals(BROKEN)) {
                    Util.log("Informing the User that " + inform.getSender().getLocalName() + " is in " +
                            inform.getContent() + " state!");
                }
            }
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            Util.log("Received subscription refuse from " + refuse.getSender().getLocalName()
                    + " with the following content: '" + refuse.getContent() + "'");
        }
    }

    /**
     * Class that implements the behaviour for handling all the incoming messages from the agents
     */
    public class HandleRequests extends CyclicBehaviour{

        @Override
        public void action() {
            ACLMessage message = receive(MessageTemplate.MatchContent(BUY_LIST));

            if (message != null){
                Util.log("The " + message.getSender().getLocalName() + " asked to buy the following list:\n"+
                        message.getUserDefinedParameter("list"));
            }
        }
    }

    // getting information from agents
    public void getHeatTemperature(AID agent){
        this.addBehaviour(new GetHeatTemperatureBehaviour(agent));
    }
    public void getFridgeList(AID fridge){
        this.addBehaviour(new GetFridgeList(fridge));
    }
    public void getDoorState(AID doors){
        this.addBehaviour(new GetDoorState(doors));
    }

    // asking agents to set some variable
    public void setHeatTemperature(AID agents, double temperature){
        this.addBehaviour(new SetHeatTemperatureBehaviour(agents, temperature));
    }
    public void changeDoorState(AID agent, DoorStates newStatus) {
        this.addBehaviour(new ChangeDoorState(agent, newStatus));
    }

    public void startHeat(AID agent){ this.addBehaviour(new StartHeatBehaviour(agent));}
    public void startSunFilter(AID agent){ this.addBehaviour(new StartSunFilter(agent));}
    public void stopHeat(AID agent){ this.addBehaviour(new StopHeatBehaviour(agent));}
    public void stopSunFilter(AID agent){ this.addBehaviour(new StopSunFilter(agent));}
    public void openShutter(AID agent){ this.addBehaviour(new OpenShutter(agent));}
    public void closeShutter(AID agent){ this.addBehaviour(new CloseShutter(agent));}
}
