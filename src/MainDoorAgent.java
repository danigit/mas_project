import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.tools.logging.ontology.GetAllLoggers;
import jade.util.Logger;
import sun.awt.windows.ThemeReader;

import java.util.*;

/**
 * Class that defines the MainDoorAgent. This is the main door of the apartment, so it is responsible for reporting
 * intrusions or other events like this to the ControllerAgent
 */
public class MainDoorAgent extends Agent implements HomeAutomation, MainDoor{
    private Set subscriptions = new HashSet();
    private State<DoorStates> doorState = new State<>(DoorStates.LOCKED);

    public MainDoorAgent(){
        registerO2AInterface(MainDoor.class, this);
    }

    @Override
    protected void setup() {
        super.setup();
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("MainDoorAgent has started..." + getName());
//        Util.logger.log(Logger.SEVERE, "MainDoorAgent has started...");

        // registering the services provided in the yellow pages
        String[] serviceTypes = {"control-service", "door-service"};
        String[] serviceNames = {"control-service", "door-service"};

        Util.registerService(this,serviceTypes, serviceNames);

        SubscriptionResponder.SubscriptionManager subscriptionManager = new SubscriptionResponder.SubscriptionManager() {
            @Override
            public boolean register(SubscriptionResponder.Subscription subscription) {
                subscriptions.add(subscription);
                notify(subscription);
                return true;
            }

            @Override
            public boolean deregister(SubscriptionResponder.Subscription subscription) {
                subscriptions.remove(subscription);
                return false;
            }

            public void notify(SubscriptionResponder.Subscription subscription) {
                ACLMessage notification = subscription.getMessage().createReply();
                notification.setPerformative(ACLMessage.AGREE);
                notification.setContent(AGREE);
                subscription.notify(notification);
            }
        };

        // adding behaviour to the agent
        DoorResponder doorBehaviour = new DoorResponder(this, responderTemplate, subscriptionManager);
        StateObserver<DoorStates, DoorResponder> doorStateObserver = new StateObserver<>(doorBehaviour);
        doorState.addObserver(doorStateObserver);
        addBehaviour(doorBehaviour);
        addBehaviour(new handleRequestsBehaviour());
    }

    /**
     * Class that handles the subscriptions to this agent
     */
    private static class DoorResponder extends SubscriptionResponder{
        Agent agent;
        MessageTemplate messageTemplate;
        SubscriptionManager subscriptionManager;

        public DoorResponder(Agent agent, MessageTemplate messageTemplate, SubscriptionManager subscriptionManager) {
            super(agent, messageTemplate, subscriptionManager);

            this.agent = agent;
            this.messageTemplate = messageTemplate;
            this.subscriptionManager = subscriptionManager;
        }

        @Override
        protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
          this.subscriptionManager.register(createSubscription(subscription));
          Util.log("Making subscription for agent: " + subscription.getSender().getLocalName());

          return null;
        }

        public void notifyAgents(ACLMessage informMessage) {
            Vector subscriptions = getSubscriptions();
            Util.log(String.valueOf(subscriptions.size()));
            for (int i = 0; i < subscriptions.size(); i++) {
                ((SubscriptionResponder.Subscription) subscriptions.elementAt(i)).notify(informMessage);
            }
        }
    }

    public class handleRequestsBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage message = receive();
//            MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchContent(STATE)));
            if (message != null){
                switch (message.getPerformative()){
                    case ACLMessage.REQUEST:
                        if (message.getContent().equals(STATE)) {
                            ACLMessage response = message.createReply();
                            response.setPerformative(ACLMessage.INFORM);
                            response.setConversationId("door-status");
                            response.setContent(doorState.getDoorState().toString());
                            send(response);
                        } else if(message.getContent().equals(CHANGE_STATE)){
                            ACLMessage response = message.createReply();
                            if (doorState.getDoorState() == DoorStates.BROKEN){
                                response.setPerformative(ACLMessage.FAILURE);
                            } else {
                                doorState.setDoorState(DoorStates.valueOf(message.getUserDefinedParameter("new_state")));
                                response.setPerformative(ACLMessage.INFORM);
                            }
                            response.setContent(doorState.getDoorState().toString());
                            send(response);
                        }
                        break;
                }
            } else {
                block();
            }
        }
    }

    @Override
    public void changeDoorState(DoorStates doorState){
        this.doorState.setDoorState(doorState);
        Util.log("The new door state is: " + this.doorState.getDoorState().toString());
    }
}
