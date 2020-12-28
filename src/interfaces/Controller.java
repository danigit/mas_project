package interfaces;

import jade.core.AID;

public interface Controller {
    void getHeatTemperature(AID agents);
    void getFridgeList(AID fridges);
    void getDoorState(AID doors);

    void setHeatTemperature(AID agents, double temperature);
    void startHeat(AID agent);
    void startSunFilter(AID agent);
    void stopHeat(AID agent);
    void stopSunFilter(AID agent);
    void openShutter(AID agent);
    void closeShutter(AID agent);
    void changeDoorState(AID agent, HomeAutomation.DoorStates doorStatus);
}
