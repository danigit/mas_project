package agents;

import behaviours.HandleWindowRequestsBehaviour;
import interfaces.HomeAutomation;
import interfaces.Window;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.proto.SubscriptionResponder;
import jade.util.Logger;
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

    public static final String NAME = "WindowAgent";

    private Set subscriptions = new HashSet();
    private final State<WindowStates> windowState = new State<>(WindowStates.CLOSED);

    public State<WindowStates> getWindowState(){
        return this.windowState;
    }

    public WindowAgent() {
        registerO2AInterface(Window.class, this);
    }

    @Override
    protected void setup() {
        super.setup();
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("WindowAgent has started...");

        // registering services to the yellow pages
        String[] serviceTypes = {"control-service", "window-service"};
        String[] serviceNames = {"HA-Window-control-service", "HA-window-service"};
        Util.registerService(this, serviceTypes, serviceNames);

        // getting the subscription manager
        SubscriptionResponder.SubscriptionManager subscriptionManager = Util.createSubscriptionManager(subscriptions);

        // creating the responder
        Responder windowBehaviour = new Responder(this, responderTemplate, subscriptionManager);

        // registering for state changes
        StateObserver<WindowStates, Responder> windowStateObserver = new StateObserver<>(windowBehaviour);
        windowState.addObserver(windowStateObserver);

        // adding behaviour to the window
        addBehaviour(windowBehaviour);
        addBehaviour(new HandleWindowRequestsBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fipaException) {
            fipaException.printStackTrace();
        }
    }

    public void changeWindowState(WindowStates windowState) {
        this.windowState.setValue(windowState);
    }
}
