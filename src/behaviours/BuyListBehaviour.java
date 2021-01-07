package behaviours;

import agents.ControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import interfaces.HomeAutomation;
import java.util.Map;
import java.util.stream.Collectors;

public class BuyListBehaviour extends OneShotBehaviour {
    Map<String, String> list;

    public BuyListBehaviour(Map<String, String> list){
        this.list = list;
    }

    @Override
    public void action() {
        // converting the list in a string format
        String listString = list.keySet().stream().map(key -> key + "=" + list.get(key))
                .collect(Collectors.joining(", ", "{", "}"));

        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(new AID(ControllerAgent.NAME, AID.ISLOCALNAME));
        message.setContent(HomeAutomation.BUY_LIST);
        message.addUserDefinedParameter("list", listString);
        myAgent.send(message);
    }
}