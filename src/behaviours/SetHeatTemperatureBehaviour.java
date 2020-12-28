package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Util;

public class SetHeatTemperatureBehaviour extends Behaviour {
    private AID[] heats;
    private int step, responses;
    private double temperature;

    public SetHeatTemperatureBehaviour(){
        step = 0;
        responses = 0;
    }
    public SetHeatTemperatureBehaviour(AID agent, double temperature) {
        this.heats = new AID[]{agent};
        this.temperature = temperature;
        step = 0;
        responses = 0;
    }

    public SetHeatTemperatureBehaviour(AID[] agents, double temperature){
        this.heats = agents;
        this.temperature = temperature;
        step = 0;
        responses = 0;
    }

    @Override
    public void action() {
        AID[] result;

        if (heats.length > 0) {
            result = heats;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "door-service");
            if (descriptions != null) {
                result = Util.getAIDFromDescriptions(descriptions);
            } else{
                result = null;
            }
        }

        switch (step) {
            case 0:
                if (result != null && result.length > 0) {
                    ACLMessage intentionMessage = new ACLMessage(ACLMessage.REQUEST);
                    intentionMessage.setContent(HomeAutomation.HeatStates.SET_TEMPERATURE.toString());
                    intentionMessage.addUserDefinedParameter("temp", String.valueOf(this.temperature));
                    intentionMessage.setConversationId("temp-set");

                    for (AID receiver : result) {
                        intentionMessage.addReceiver(receiver);
                        Util.log("Asking to agent " + receiver.getLocalName() + " to set the following temperature: " +
                                this.temperature);
                    }

                    myAgent.send(intentionMessage);
                    responses = result.length;
                    step++;
                }else{
                    Util.log("No heat-service found!");
                    step = 2;
                }
                break;
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("temp-set"));

                if (response != null){
                    Util.log( "The agent " + response.getSender().getLocalName() + " has communicated to the Controller "+
                            "that the temperature was set at: " + response.getContent());
                    Util.log("Communicating to the User tha the agent " + response.getSender().getLocalName() + " has set " +
                            "the temperature at: " + response.getContent());
                    responses--;

                    if (responses == 0) {
                        step++;
                    }
                } else{
                    block();
                }
                break;
        }
    }

    @Override
    public boolean done() {
        return step == 2;
    }
}
