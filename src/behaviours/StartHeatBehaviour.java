package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class StartHeatBehaviour extends Behaviour {
    private AID[] agents;
    private int step, responses;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public StartHeatBehaviour() {
        step = 0;
        responses = 0;
    }

    public StartHeatBehaviour(AID agent){
        this();
        this.agents = new AID[]{agent};
    }

    public StartHeatBehaviour(AID[] agents){
        this();
        this.agents = agents;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "heat-service");

        switch (step){
            case 0:
                if (result != null && result.length > 0) {
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.START);
                    message.setConversationId("start-heat");

                    for (AID agent : result) {
                        message.addReceiver(agent);
                        Util.log("Asking to the agent " + agent.getLocalName() + " to go in start state");
                    }

                    myAgent.send(message);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No door-service found!");
                    step = 2;
                }
                break;
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("start-heat"));

                if (response != null){
                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that "+
                            "the heat is on");
                    Util.log("Informing the User that the " + response.getSender().getLocalName() + " is in state: "+
                            response.getContent());

                    responses--;
                    if (responses == 0){
                        step++;
                    }
                } else{
                    block();
                }
                break;
            default: step = 2;
        }
    }

    @Override
    public boolean done(){
        return step == 2;
    }
}