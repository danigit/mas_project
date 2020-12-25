import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import java.security.acl.Acl;


public class HeatAgent extends Agent implements HomeAutomation{

    private HeatStates heatState = HeatStates.STOP;
    private double heatValue = 26.0;

    @Override
    protected void setup() {
        super.setup();

        Util.logger = Logger.getMyLogger(getLocalName());

        Util.log("HeatAgent has started...");
//        Util.logger.log(Logger.INFO, "HeatAgent has started...");

        String[] serviceTypes = {"heat-service"};
        String[] serviceNames = {"HA-heat-service"};

        Util.registerService(this,serviceTypes, serviceNames);

        // adding behaviour to the window
        addBehaviour(new HeatBehaviour());
    }

    private class HeatBehaviour extends OneShotBehaviour{

        @Override
        public void action() {
            ACLMessage requestMessage = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

            if (requestMessage != null){
                String messageContent = requestMessage.getContent();
                if (HeatStates.START.toString().equals(messageContent)) {
                    heatState = HeatStates.START;
                    Util.log("The heat is on!");
                    Util.logger.log(Logger.INFO, "The heat is on!");

                    ACLMessage reply = requestMessage.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(heatState.toString());
                    send(reply);
                } else if (HeatStates.STOP.toString().equals(messageContent)){
                    heatState = HeatStates.STOP;
                    Util.log("The heat is off!");
                    Util.logger.log(Logger.INFO, "The heat is off!");

                    ACLMessage reply = requestMessage.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(heatState.toString());
                    send(reply);
                } else if (HeatStates.GET_TEMPERATURE.toString().equals(messageContent)){
                    ACLMessage responseMessage = requestMessage.createReply();
                    requestMessage.setPerformative(ACLMessage.INFORM);
                    responseMessage.setContent(String.valueOf(heatValue));
                    send(responseMessage);
                } else if (HeatStates.SET_TEMPERATURE.toString().equals(messageContent)){
                    String temp = requestMessage.getUserDefinedParameter("temp");
                    Util.log("Setted the temperature to: " + temp);
                    Util.logger.log(Logger.INFO, "Setted the temperature to: " + temp);
                    heatValue = Double.parseDouble(temp);

                    ACLMessage reply = requestMessage.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(String.valueOf(heatValue));
                    send(reply);
                } else{
                    ACLMessage responseMessage = requestMessage.createReply();
                    responseMessage.setPerformative(ACLMessage.INFORM);
                    responseMessage.setContent(UNKNOWN_COMMAND);
                    send(responseMessage);
                }
            }
        }
    }

//    private class HeatBehaviour extends CyclicBehaviour{
//
//        @Override
//        public void action() {
//            ACLMessage requestMessage = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
//
//            if (requestMessage != null){
//                String messageContent = requestMessage.getContent();
//
//                if(messageContent.equals(HeatAgent.START.toString())){
//                    heatState = HeatAgent.START;
//                    Util.log("The heat is on!");
//                    ACLMessage reply = requestMessage.createReply();
//                    reply.setPerformative(ACLMessage.CONFIRM);
//                    reply.setContent(heatState.toString());
//                    send(reply);
//                } else if (messageContent.equals(HeatAgent.STOP.toString())){
//                    heatState = HeatAgent.STOP;
//                    Util.log("The heat is off!");
//                    ACLMessage reply = requestMessage.createReply();
//                    reply.setPerformative(ACLMessage.CONFIRM);
//                    reply.setContent(heatState.toString());
//                    send(reply);
//                } else if (messageContent.equals(HeatAgent.GET_TEMPERATURE.toString())){
//                    // here we should use INFORM-RESULT but JADE does not support it yet
//                    ACLMessage responseMessage = new ACLMessage(ACLMessage.INFORM);
//                    responseMessage.setContent(String.valueOf(heatValue));
//                    responseMessage.addReceiver(new AID(ControllerAgent.NAME, AID.ISLOCALNAME));
//                    send(responseMessage);
//                } else if(messageContent.equals(HeatAgent.SET_TEMPERATURE.toString())){
//                    String temp = requestMessage.getUserDefinedParameter("temp");
//                    Util.log("The temperature is " + temp);
//                    heatValue = Double.parseDouble(temp);
//                    ACLMessage reply = requestMessage.createReply();
//                    reply.setPerformative(ACLMessage.CONFIRM);
//                    reply.setContent(String.valueOf(heatValue));
//                    send(reply);
//                } else{
//                    // here we should use INFORM-RESULT but JADE does not support it yet
//                    ACLMessage responseMessage = new ACLMessage(ACLMessage.INFORM);
//                    responseMessage.setContent(UNKNOWN_COMMAND);
//                    responseMessage.addReceiver(new AID(CONTROLLER, AID.ISLOCALNAME));
//                    send(responseMessage);
//                }
//            } else{
//                block();
//            }
//        }
//    }

    public void changeTemperatureValue(double temperatureValue){
        this.heatValue = temperatureValue;
    }
}
