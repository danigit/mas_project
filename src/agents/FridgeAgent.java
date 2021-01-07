package agents;

import behaviours.BuyListBehaviour;
import behaviours.HandleFridgeRequestsBehaviour;
import interfaces.Fridge;
import interfaces.HomeAutomation;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
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

    public static final String NAME = "FridgeAgent";

    private Set subscriptions = new HashSet();
    private final State<FridgeStates> fridgeState = new State<>(FridgeStates.RUNNING);
    private Map<String, String> list = new HashMap<>();

    public FridgeAgent(){
        registerO2AInterface(Fridge.class, this);
    }

    public Map<String, String> getList(){
        return this.list;
    }
    public State<FridgeStates> getFridgeState(){ return this.fridgeState; }

    @Override
    protected void setup() {
        super.setup();
        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("FridgeAgent has started...");

        // a complete implementation should have a list for the things that are in the fridge
        // and a list for the things that should be in the fridge
        fillList();

        // registering services to yellow pages
        String[] serviceTypes = {"control-service", "fridge-service"};
        String[] serviceNames = {"HA-Fridge-control-service", "HA-fridge-service"};
        Util.registerService(this, serviceTypes, serviceNames);

        // creating the subscription manager
        SubscriptionResponder.SubscriptionManager subscriptionManager = Util.createSubscriptionManager(subscriptions);

        // creating the responder
        Responder fridgeBehaviour = new Responder(this, responderTemplate, subscriptionManager);

        // registering for state changes
        StateObserver<FridgeStates, Responder> fridgeStatesObserver = new StateObserver<>(fridgeBehaviour);
        fridgeState.addObserver(fridgeStatesObserver);

        // adding behaviours to the agent
        addBehaviour(fridgeBehaviour);
        addBehaviour(new HandleFridgeRequestsBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fipaException) {
            fipaException.printStackTrace();
        }
    }

    public void changeFridgeState(FridgeStates fridgeState){
        this.fridgeState.setValue(fridgeState);
    }

    public void buyList(){
        addBehaviour(new BuyListBehaviour(list));
    }

    private void fillList(){
        list.put("milk", "2l");
        list.put("eggs", "5");
        list.put("tomatoes", "1kg");
    }
}
