import com.sun.org.apache.xpath.internal.functions.FuncSubstring;

public interface HomeAutomation {

    // enum that define the heat agent services
    enum HeatAgent{
        GET_TEMPERATURE,
        SET_TEMPERATURE
    }

    // enum that defines the states of the Door agent
    enum DoorStates{
        LOCKED,
        UNLOCKED,
        FINGERPRINT_VALID,
        FINGERPRINT_NOT_VALID,
        BROKEN
    }

    // enum that defines the Window agent states
    enum WindowStates{
        CLOSED,
        OPENED,
        BROKEN
    }

    // enum that defines the Shutter agent states
    enum ShutterStates{
        UP,
        DOWN,
        BROKEN
    }
}
