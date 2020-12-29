package agents;

import behaviours.HandleShutterRequestsBehaviour;
import interfaces.HomeAutomation;
import interfaces.Shutter;
import jade.core.Agent;
import jade.proto.SubscriptionResponder;
import jade.util.Logger;
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

    public static String NAME = "ShutterAgent";

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
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("ShutterAgent has started...");

        // registering services to the yellow pages
        String[] serviceTypes = {"control-service", "shutter-service"};
        String[] serviceNames = {"HA-control-service", "HA-shutter-service"};
        Util.registerService(this, serviceTypes, serviceNames);

        // getting the subscription manager
        SubscriptionResponder.SubscriptionManager subscriptionManager = Util.createSubscriptionManager(subscriptions);

        // creating the responder
        Responder shutterBehaviour = new Responder(this, responderTemplate, subscriptionManager);

        // registering for state changes
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
