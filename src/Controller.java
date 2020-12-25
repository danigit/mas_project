import jade.core.AID;
import jade.core.Agent;

public interface Controller {
    void getTemperature();
    void setTemperature(AID[] agents, double temperature);
    void getDoorStatus();
    void changeDoorStatus(AID agent, HomeAutomation.DoorStates doorStatus);
}
