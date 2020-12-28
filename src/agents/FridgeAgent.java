package agents;

import behaviours.BuyListBehaviour;
import behaviours.HandleFridgeRequestsBehaviour;
import interfaces.Fridge;
import interfaces.HomeAutomation;
import jade.core.Agent;
import jade.proto.SubscriptionResponder;
import jade.util.Logger;
import utils.Responder;
import utils.State;
import utils.StateObserver;
import utils.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that defines the FridgeAgent. This agent is responsible for informing the ControllerAgent what is the
 * food situation in apartment
 */
public class FridgeAgent extends Agent implements HomeAutomation, Fridge {

    private static final String NAME = "FridgeAgent";

    private Set subscriptions = new HashSet();
    private State<FridgeStates> fridgeState = new State<>(FridgeStates.RUNNING);
    private Map<String, String> list = new HashMap<>();

    public FridgeAgent(){
        registerO2AInterface(Fridge.class, this);
    }

    public Map<String, String> getList(){
        return this.list;
    }

    @Override
    protected void setup() {
        super.setup();
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("FridgeAgent has started...");

        list.put("milk", "2l");
        list.put("eggs", "5");
        list.put("tomatoes", "1kg");

        // I can register multiple services at once
        String[] serviceTypes = {"control-service", "fridge-service"};
        String[] serviceNames = {"HA-control-service", "HA-fridge-service"};

        Util.registerService(this, serviceTypes, serviceNames);

        // adding behaviour to the window
        SubscriptionResponder.SubscriptionManager subscriptionManager = Util.createSubscriptionManager(subscriptions);

        Responder fridgeBehaviour = new Responder(this, responderTemplate, subscriptionManager);
        StateObserver<FridgeStates, Responder> fridgeStatesObserver = new StateObserver<>(fridgeBehaviour);
        fridgeState.addObserver(fridgeStatesObserver);

        // adding behaviours to the agent
        addBehaviour(fridgeBehaviour);
        addBehaviour(new HandleFridgeRequestsBehaviour(this));
    }

    public void changeFridgeState(FridgeStates fridgeState){
        this.fridgeState.setValue(fridgeState);
    }
    public void buyList(){
        addBehaviour(new BuyListBehaviour(list));
    }
}
