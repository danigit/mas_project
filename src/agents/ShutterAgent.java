package agents;

import behaviours.HandleShutterRequestsBehaviour;
import interfaces.HomeAutomation;
import interfaces.Shutter;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionResponder;
import utils.Responder;
import utils.State;
import utils.StateObserver;
import utils.Util;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that defines the ShutterAgent. This class is responsible for lowering/raising the shutter. This
 * event could happen at a precise hour or may be triggered by some other agent
 */
public class ShutterAgent extends Agent implements HomeAutomation, Shutter {

    private Set subscriptions = new HashSet();
    private State<ShutterStates> shutterState = new State<>(ShutterStates.DOWN);

    public void setShutterState(ShutterStates shutterState){
        this.shutterState.setValue(shutterState);
    }
    public State<ShutterStates> getShutterState(){
        return this.shutterState;
    }

    public ShutterAgent(){
        registerO2AInterface(Shutter.class, this);
    }
    @Override
    protected void setup() {
        super.setup();

        System.out.println("ShutterAgent starting...");

        String[] serviceTypes = {"control-service", "shutter-service"};
        String[] serviceNames = {"HA-control-service", "HA-shutter-service"};

        Util.registerService(this, serviceTypes, serviceNames);

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
        };//Util.createSubscriptionManager(subscriptions);

        Responder shutterBehaviour = new Responder(this, responderTemplate, subscriptionManager);
        StateObserver<ShutterStates, Responder> shutterStatesObserver = new StateObserver<>(shutterBehaviour);
        shutterState.addObserver(shutterStatesObserver);

        // adding behaviour to the agent
        addBehaviour(shutterBehaviour);
        addBehaviour(new HandleShutterRequestsBehaviour(this));
    }

    public void changeShutterStatus(ShutterStates shutterState){
        this.shutterState.setValue(shutterState);
    }
}
