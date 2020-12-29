package behaviours;

import agents.HeatAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleHeatRequestsBehaviour extends CyclicBehaviour {

    private HeatAgent agent;

    public HandleHeatRequestsBehaviour(HeatAgent agent){
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();

        if (message != null){
            ACLMessage response = message.createReply();
            String messageContent = message.getContent();

            switch (message.getPerformative()){
                case ACLMessage.REQUEST:
                    if (agent.getHeatState() != HomeAutomation.HeatStates.BROKEN) {
                        if (HomeAutomation.START.equals(messageContent)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.setHeatState(HomeAutomation.HeatStates.RUNNING);
                            response.setContent(agent.getHeatState().toString());
                        } else if (HomeAutomation.STOP.equals(messageContent)) {
                            response.setPerformative(ACLMessage.INFORM);
                            agent.setHeatState(HomeAutomation.HeatStates.SHUT_DOWN);
                            response.setContent(agent.getHeatState().toString());
                        } else if (HomeAutomation.GET_TEMPERATURE.equals(messageContent)) {
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(String.valueOf(agent.getHeatTemperature()));
                        } else if (HomeAutomation.SET_TEMPERATURE.equals(messageContent)) {
                            response.setPerformative(ACLMessage.INFORM);
                            String temp = message.getUserDefinedParameter("temp");
                            agent.setHeatTemperature(Double.parseDouble(temp));
                            response.setContent(String.valueOf(agent.getHeatTemperature()));
                        } else {
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(HomeAutomation.UNKNOWN_COMMAND);
                        }
                    } else{
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Unable to access the Heat because is broken.");
                    }
                    myAgent.send(response);
                    break;
            }
        } else {
            block();
        }
    }
}