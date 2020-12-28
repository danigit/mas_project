package interfaces;

import interfaces.HomeAutomation;

public interface Shutter {
    void changeShutterStatus(HomeAutomation.ShutterStates shutterState);
}
