import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public interface HomeAutomation {

    String CONTROLLER = "Controller";
    String PERSON = "Person";
    String UNKNOWN_COMMAND = "Unknown command";
    String STATE = "STATE";
    String BROKEN = "BROKEN";
    String CHANGE_STATE = "CHANGE_STATE";
    String AGREE = "AGREE";

    MessageTemplate responderTemplate = MessageTemplate
            .and(MessageTemplate
                .or(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE),
                    MessageTemplate.MatchPerformative(ACLMessage.CANCEL)),
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE));

    // enum that define the heat agent services
    enum HeatStates{
        START,
        STOP,
        GET_TEMPERATURE,
        SET_TEMPERATURE
    }

    // enum that defines the states of the Door agent
    enum DoorStates{
        LOCKED,
        UNLOCKED,
        BROKEN,
        NOT_BROKEN
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

    // enum that defines the Fridge agent states
    enum FridgeStates{
        RUNNING,
        BROKEN
    }
}
