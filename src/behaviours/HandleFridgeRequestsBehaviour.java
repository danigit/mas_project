package behaviours;

import agents.FridgeAgent;
import interfaces.HomeAutomation;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.stream.Collectors;

public class HandleFridgeRequestsBehaviour extends CyclicBehaviour {
    FridgeAgent agent;

    public HandleFridgeRequestsBehaviour(FridgeAgent agent){
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive();

        if (message != null){
            ACLMessage response = message.createReply();
            String messageContent = message.getContent();

            switch (message.getPerformative()){
                // handling the request messages
                case ACLMessage.REQUEST:
                    if (agent.getFridgeState().getValue() != HomeAutomation.FridgeStates.BROKEN) {
                        if (messageContent.equals(HomeAutomation.GET_LIST)) {
                            // converting the list into a string
                            String listString = agent.getList().keySet().stream().map(key -> key + "=" + agent.getList().get(key))
                                    .collect(Collectors.joining(", ", "{", "}"));

                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(listString);
                        } else{
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent(HomeAutomation.UNKNOWN_COMMAND);
                        }
                        myAgent.send(response);
                    } else {
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Unable to access the Fridge because is broken.");
                        myAgent.send(response);
                    }
                    break;
            }
        } else{
            block();
        }
    }
}