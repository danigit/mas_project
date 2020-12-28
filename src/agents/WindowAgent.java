package agents;

import behaviours.HandleWindowRequestsBehaviour;
import interfaces.HomeAutomation;
import interfaces.Window;
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
 * Class that defines a WindowAgent. This class represents a window of the apartment, and it is responsible
 * for detecting events like intrusions or when is forced
 */
public class WindowAgent extends Agent implements HomeAutomation, Window {

    private Set subscriptions = new HashSet();
    private State<WindowStates> windowState = new State<>(WindowStates.CLOSED);

    public void setWindowState(WindowStates windowState){
        this.windowState.setValue(windowState);
    }
    public State<WindowStates> getWindowState(){
        return this.windowState;
    }

    public WindowAgent() {
        registerO2AInterface(Window.class, this);
    }

    @Override
    protected void setup() {
        super.setup();

        System.out.println("WindowAgent started...");

        // I can register multiple services at once
        String[] serviceTypes = {"control-service", "window-service"};
        String[] serviceNames = {"HA-control-service", "HA-window-service"};

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

        Responder windowBehaviour = new Responder(this, responderTemplate, subscriptionManager);
        StateObserver<WindowStates, Responder> windowStateObserver = new StateObserver<>(windowBehaviour);
        windowState.addObserver(windowStateObserver);

        // adding behaviour to the window
        addBehaviour(windowBehaviour);
        addBehaviour(new HandleWindowRequestsBehaviour(this));
    }

    public void changeWindowStatus(WindowStates windowState) {
        this.windowState.setValue(windowState);
    }
}
