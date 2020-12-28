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
        ACLMessage requestMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        if (requestMessage != null){
            String messageContent = requestMessage.getContent();
            ACLMessage reply = requestMessage.createReply();
            reply.setPerformative(ACLMessage.INFORM);

            if (HomeAutomation.HeatStates.START.toString().equals(messageContent)) {
                agent.setHeatState(HomeAutomation.HeatStates.START);
                reply.setContent(agent.getHeatState().toString());
            } else if (HomeAutomation.HeatStates.STOP.toString().equals(messageContent)){
                agent.setHeatState(HomeAutomation.HeatStates.STOP);
                reply.setContent(agent.getAgentState().toString());
            } else if (HomeAutomation.HeatStates.GET_TEMPERATURE.toString().equals(messageContent)){
                reply.setContent(String.valueOf(agent.getHeatTemperature()));
            } else if (HomeAutomation.HeatStates.SET_TEMPERATURE.toString().equals(messageContent)){
                String temp = requestMessage.getUserDefinedParameter("temp");
                agent.setHeatTemperature(Double.parseDouble(temp));
                reply.setContent(String.valueOf(agent.getHeatTemperature()));
            } else{
                reply.setContent(HomeAutomation.UNKNOWN_COMMAND);
            }
            myAgent.send(reply);
        } else {
            block();
        }
    }
}