import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.tools.logging.ontology.GetAllLoggers;
import jade.util.Logger;

import java.util.*;

/**
 * Class that defines the MainDoorAgent. This is the main door of the apartment, so it is responsible for reporting
 * intrusions or other events like this to the ControllerAgent
 */
public class MainDoorAgent extends Agent implements HomeAutomation{
    private Set subscriptions = new HashSet();
    private State<DoorStates> doorState = new State<>(DoorStates.LOCKED);
    private StateObserver<DoorStates, DoorResponder> doorStateObserver;

    @Override
    protected void setup() {
        super.setup();
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("MainDoorAgent has started...");
        Util.logger.log(Logger.SEVERE, "MainDoorAgent has started...");

        // registering the services provided in the yellow pages
        Util.registerService(this,"control-service", "HA-door-service");

        SubscriptionResponder.SubscriptionManager subscriptionManager = new SubscriptionResponder.SubscriptionManager() {
            @Override
            public boolean register(SubscriptionResponder.Subscription subscription) throws RefuseException, NotUnderstoodException {
                subscriptions.add(subscription);
                notify(subscription);
                return true;
            }

            @Override
            public boolean deregister(SubscriptionResponder.Subscription subscription) throws FailureException {
                subscriptions.remove(subscription);
                return false;
            }

            public void notify(SubscriptionResponder.Subscription subscription) {
                ACLMessage notification = subscription.getMessage().createReply();
                notification.setPerformative(ACLMessage.AGREE);
                subscription.notify(notification);
            }
        };

        // adding behaviour to the agent
        DoorResponder doorBehaviour = new DoorResponder(this, responderTemplate, subscriptionManager);
        doorStateObserver = new StateObserver<>(doorBehaviour);
        doorState.addObserver(doorStateObserver);
        addBehaviour(doorBehaviour);

        addBehaviour(new TickerBehaviour(this, 2000) {

            @Override
            protected void onTick() {
                Random random = new Random();
                if (random.nextInt() % 2 == 0){
                    doorState.setDoorState(DoorStates.BROKEN);
                } else {
                    doorState.setDoorState(DoorStates.LOCKED);
                }
            }
        });
    }

    /**
     * Class that handles the subscriptions to this agent
     */
    private class DoorResponder extends SubscriptionResponder{
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
            for (int i = 0; i < subscriptions.size(); i++) {
                ((SubscriptionResponder.Subscription) subscriptions.elementAt(i)).notify(informMessage);
            }
        }
    }

    public void changeDoorState(State<DoorStates> doorState){
        this.doorState = doorState;
    }
}
