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
            switch (message.getPerformative()){
                case ACLMessage.REQUEST:
                    if (message.getContent().equals(HomeAutomation.FridgeStates.GET_LIST.toString()) &&
                            message.getConversationId().equals("get-list")){
                        String listString = agent.getList().keySet().stream().map(key -> key + "=" + agent.getList().get(key))
                                .collect(Collectors.joining(", ", "{", "}"));

                        ACLMessage response = message.createReply();
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent(listString);
                        myAgent.send(response);
                    }
                    break;
            }
        } else{
            block();
        }
    }
}