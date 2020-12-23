import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class HeatAgent extends Agent implements HomeAutomation{

    private HeatAgent heatState = HeatAgent.STOP;
    private double heatValue = 26.0;

    @Override
    protected void setup() {
        super.setup();

        System.out.println("HeatAgent started...");

        Util.registerService(this,"heat-service", "HA-heat-service");

        // adding behaviour to the window
        // has pretty much the same behaviour as the door
        addBehaviour(new HeatBehaviour());
    }

    private class HeatBehaviour extends CyclicBehaviour{

        @Override
        public void action() {
            ACLMessage requestMessage = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

            if (requestMessage != null){
                String messageContent = requestMessage.getContent();

                if(messageContent.equals(HeatAgent.START.toString())){
                    heatState = HeatAgent.START;
                    Util.log("The heat is on!");
                    ACLMessage reply = requestMessage.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(heatState.toString());
                    send(reply);
                } else if (messageContent.equals(HeatAgent.STOP.toString())){
                    heatState = HeatAgent.STOP;
                    Util.log("The heat is off!");
                    ACLMessage reply = requestMessage.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(heatState.toString());
                    send(reply);
                } else if (messageContent.equals(HeatAgent.GET_TEMPERATURE.toString())){
                    // here we should use INFORM-RESULT but JADE does not support it yet
                    ACLMessage responseMessage = new ACLMessage(ACLMessage.INFORM);
                    responseMessage.setContent(String.valueOf(heatValue));
                    responseMessage.addReceiver(new AID(ControllerAgent.NAME, AID.ISLOCALNAME));
                    send(responseMessage);
                } else if(messageContent.equals(HeatAgent.SET_TEMPERATURE.toString())){
                    String temp = requestMessage.getUserDefinedParameter("temp");
                    Util.log("The temperature is " + temp);
                    heatValue = Double.parseDouble(temp);
                    ACLMessage reply = requestMessage.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(String.valueOf(heatValue));
                    send(reply);
                } else{
                    // here we should use INFORM-RESULT but JADE does not support it yet
                    ACLMessage responseMessage = new ACLMessage(ACLMessage.INFORM);
                    responseMessage.setContent(UNKNOWN_COMMAND);
                    responseMessage.addReceiver(new AID(CONTROLLER, AID.ISLOCALNAME));
                    send(responseMessage);
                }
            } else{
                block();
            }
        }
    }

    public void changeTemperatureValue(double temperatureValue){
        this.heatValue = temperatureValue;
    }
}
