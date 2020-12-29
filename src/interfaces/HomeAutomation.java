package interfaces;

import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public interface HomeAutomation {

    String UNKNOWN_COMMAND = "UNKNOWN COMMAND";
    String AGREE = "AGREE";
    String BROKEN = "BROKEN";
    String START = "START";
    String START_SUNFILTER = "START_SUNFILTER";
    String STOP = "STOP";
    String STOP_SUNFILTER = "STOP_SUNFILTER";
    String GET_STATE = "GET_STATE";
    String GET_LIST = "GET_LIST";
    String GET_TEMPERATURE = "GET_TEMPERATURE";
    String CHANGE_STATE = "CHANGE_STATE";
    String BUY_LIST = "BUY_LIST";
    String SET_TEMPERATURE = "SET_TEMPERATURE";
    String UP_SHUTTER = "UP_SHUTTER";
    String DOWN_SHUTTER = "DOWN_SHUTTER";

    MessageTemplate responderTemplate = MessageTemplate.and(MessageTemplate
                .or(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE),
                    MessageTemplate.MatchPerformative(ACLMessage.CANCEL)),
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE));

    // enum that define the heat agent services
    enum HeatStates{
        RUNNING,
        SHUT_DOWN,
        BROKEN
    }

    // enum that defines the states of the Door agent
    enum DoorStates{
        LOCKED,
        UNLOCKED,
        BROKEN,
    }

    // enum that defines the Window agent states
    enum WindowStates{
        CLOSED,
        OPENED,
        SUNFILTER_OFF,
        SUNFILTER_ON,
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
        NOT_RUNNING,
        BROKEN,
    }
}
