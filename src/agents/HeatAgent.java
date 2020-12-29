package agents;

import behaviours.HandleHeatRequestsBehaviour;
import interfaces.HomeAutomation;
import jade.core.Agent;
import jade.util.Logger;
import utils.Util;

public class HeatAgent extends Agent implements HomeAutomation {

    public static String NAME = "HeatAgent";

    private HeatStates heatState = HeatStates.SHUT_DOWN;
    private double heatValue = 26.0;

    public void setHeatTemperature(double temperature){
        this.heatValue = temperature;
    }
    public double getHeatTemperature(){
        return this.heatValue;
    }
    public void setHeatState(HeatStates heatState) {
        this.heatState = heatState;
    }
    public HeatStates getHeatState(){
        return this.heatState;
    }

    @Override
    protected void setup() {
        super.setup();

        Util.logger = Logger.getMyLogger(getLocalName());
        Util.log("HeatAgent has started...");

        // registering services to yellow pages
        String[] serviceTypes = {"heat-service"};
        String[] serviceNames = {"HA-heat-service"};
        Util.registerService(this,serviceTypes, serviceNames);

        // adding behaviour to the window
        addBehaviour(new HandleHeatRequestsBehaviour(this));
    }

    public void changeHeatState(HeatStates heatState){
        this.heatState = heatState;
    }
}
