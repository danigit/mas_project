package behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import interfaces.HomeAutomation;
import utils.Util;

public class StopSunFilter extends Behaviour {
    private AID[] agents;
    private int step, responses;

    // constructors that allow to pass none, one or multiple agents
    // if none agent is passed then I take all the agents that provide the door service
    public StopSunFilter(){
        step = 0;
        responses = 0;
    }

    public StopSunFilter(AID agent){
        this();
        this.agents = new AID[]{agent};
    }

    public StopSunFilter(AID[] agents) {
        this();
        this.agents = agents;
    }

    @Override
    public void action() {
        AID[] result = Util.getAgentsList(myAgent, agents, "window-service");

        switch (step){
            // sending the requests
            case 0:
                if (result != null && result.length > 0){
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setContent(HomeAutomation.STOP_SUNFILTER);
                    message.setConversationId("stop-sunfilter");

                    for (AID agent : result) {
                        Util.log("Asking to the agent " + agent.getLocalName() + " to stop the sun filter");
                        message.addReceiver(agent);
                    }

                    myAgent.send(message);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No 'window-service' found");
                    step = 2;
                }
                break;
            // getting the response
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("stop-sunfilter"));

                if (response != null) {
                    switch (response.getPerformative()) {
                        case ACLMessage.INFORM:
                            Util.log("The agent " + response.getSender().getLocalName() + " has informed the Controller that " +
                                    "the sun filter in off");
                            Util.log("Informing the user that the agent " + response.getSender().getLocalName() + " that the " +
                                    "the sun filters are in state: " + response.getContent());
                            break;
                        case ACLMessage.FAILURE:
                            Util.log("Agent " + response.getSender().getLocalName() + " has sent the following message "+
                                    "to the ControllerAgent: " + response.getContent());
                            Util.log("Informing the User that the the agent " + response.getSender().getLocalName() +
                                    " is broken");
                            break;
                    }

                    // controlling that all the agents have answered
                    responses--;
                    if (responses == 0) {
                        step++;
                    }
                } else {
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