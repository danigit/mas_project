package behaviours;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import utils.*;

public class GetHeatTemperatureBehaviour extends Behaviour {
    AID[] heats;
    int step, responses;

    public GetHeatTemperatureBehaviour(){
        step = 0;
        responses = 0;
    }

    public GetHeatTemperatureBehaviour(AID agent) {
        this.heats = new AID[]{agent};
        step = 0;
        responses = 0;
    }

    public GetHeatTemperatureBehaviour(AID[] heatAgents){
        this.heats = heatAgents;
        step = 0;
        responses = 0;
    }

    @Override
    public void action() {
        AID[] result;

        if (heats.length > 0) {
            result = heats;
        } else {
            DFAgentDescription[] descriptions = Util.searchDFTemplate(myAgent, "heat-service");
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
                    intentionMessage.setContent(HomeAutomation.HeatStates.GET_TEMPERATURE.toString());
                    intentionMessage.setConversationId("temp-value");

                    for (AID agent : result) {
                        intentionMessage.addReceiver(agent);
                        Util.log("Sending request for temperature to the agent " + agent.getLocalName());
                    }

                    myAgent.send(intentionMessage);
                    responses = result.length;
                    step++;
                } else{
                    Util.log("No 'heat-service' found!");
                    step = 2;
                }
                break;
            case 1:
                ACLMessage response = myAgent.receive(MessageTemplate.MatchConversationId("temp-value"));
                if (response != null){

                    Util.log("The agent " + response.getSender().getLocalName() + " has informed the controller that "+
                            "the temperature is: " + response.getContent());
                    responses--;

                    Util.log("Informing the User that the temperature is: " + response.getContent());
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