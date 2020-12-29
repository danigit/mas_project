package agents;

import behaviours.HandleDoorRequestsBehaviour;
import interfaces.HomeAutomation;
import interfaces.MainDoor;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionResponder;
import jade.util.Logger;
import utils.Responder;
import utils.State;
import utils.StateObserver;
import utils.Util;

import java.util.*;

/**
 * Class that defines the MainDoorAgent. This is the main door of the apartment, so it is responsible for reporting
 * intrusions or other events like this to the ControllerAgent
 */
public class MainDoorAgent extends Agent implements HomeAutomation, MainDoor {
    private Set subscriptions = new HashSet();
    private State<DoorStates> doorState = new State<>(DoorStates.LOCKED);

    public State<DoorStates> getDoorState(){
        return this.doorState;
    }
    public void setDoorState(DoorStates doorState){
        this.doorState.setValue(doorState);
    }

    public MainDoorAgent(){
        registerO2AInterface(MainDoor.class, this);
    }

    @Override
    protected void setup() {
        super.setup();
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("MainDoorAgent has started...");
        Util.logger.log(Logger.SEVERE, "MainDoorAgent has started...");

        // registering the services provided in the yellow pages
        String[] serviceTypes = {"control-service", "door-service"};
        String[] serviceNames = {"HA-Door-control-service", "HA-door-service"};
        Util.registerService(this, serviceTypes, serviceNames);

        SubscriptionResponder.SubscriptionManager subscriptionManager = new SubscriptionResponder.SubscriptionManager() {
            @Override
            public boolean register(SubscriptionResponder.Subscription subscription) {
                Util.log("Registering the subscription " + subscription.getMessage().getSender());
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
                Util.log("Notifying the agetn " + subscription.getMessage().getSender());
                notification.setPerformative(ACLMessage.AGREE);
                notification.setContent(AGREE);
                subscription.notify(notification);
            }
        };

        Responder doorBehaviour = new Responder(this, responderTemplate, subscriptionManager);
        StateObserver<DoorStates, Responder> doorStateObserver = new StateObserver<>(doorBehaviour);
        doorState.addObserver(doorStateObserver);

        // adding behaviour to the agent
        addBehaviour(doorBehaviour);
//        addBehaviour(new HandleDoorRequestsBehaviour(this));
    }

    public void changeDoorState(DoorStates doorState){
        this.doorState.setValue(doorState);
    }
}
