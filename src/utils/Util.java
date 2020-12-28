package utils;

import interfaces.HomeAutomation;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionResponder;
import jade.util.Logger;
import sun.util.resources.cldr.id.CalendarData_id_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that implements static methods that are useful to the agents
 */
public class Util implements HomeAutomation {

    public static Logger logger = null;

    /**
     * Method that register the services of and agent with the yellow pages
     * @param agent - the agent that offers the services
     * @param serviceTypes - the type of the service to be registered
     * @param serviceNames - the name of the service to be registered
     */
    public static void registerService(Agent agent, String[] serviceTypes, String[] serviceNames){
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(agent.getAID());
        ServiceDescription serviceDescription = null;
        for (int i = 0; i < serviceTypes.length; i++) {
            serviceDescription = new ServiceDescription();
            serviceDescription.setType(serviceTypes[i]);
            serviceDescription.setName(serviceNames[i]);
            agentDescription.addServices(serviceDescription);
        }

        try{
            // TODO deregister services
            DFService.register(agent, agentDescription);
        } catch (FIPAException fipaException){
            fipaException.printStackTrace();
        }
    }

    /**
     * Method that search for the agents that offer services that
     * match with the one passed as parameter
     * @param agent - the agent that search for the service
     * @param templateType - the service to be searched
     * @return and array of the agents that provide this service, or null otherwise
     */
    public static DFAgentDescription[] searchDFTemplate(Agent agent, String templateType){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(templateType);
        template.addServices(serviceDescription);

        try {
            return DFService.search(agent, template);
        } catch (FIPAException fipaException){
            fipaException.printStackTrace();
        }

        return null;
    }

    public static ACLMessage subscribeToService(Agent agent, AID defaultDF, String serviceType){
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType);
        agentDescription.addServices(serviceDescription);
        return DFService.createSubscriptionMessage(agent, defaultDF, agentDescription, null);
    }

    public static SubscriptionResponder.SubscriptionManager createSubscriptionManager(Set subscriptions){
        return new SubscriptionResponder.SubscriptionManager() {
            @Override
            public boolean register(SubscriptionResponder.Subscription subscription) {
                subscriptions.add(subscription);
                notify(subscription);
                return true;
            }

            @Override
            public boolean deregister(SubscriptionResponder.Subscription subscription) {
                subscriptions.remove(subscription);
                return false;
            }

            public void notify(SubscriptionResponder.Subscription subscription) {
                ACLMessage notification = subscription.getMessage().createReply();
                notification.setPerformative(ACLMessage.AGREE);
                notification.setContent(AGREE);
                subscription.notify(notification);
            }
        };
    }

    public static AID[] getAIDFromDescriptions(DFAgentDescription[] descriptions){
        AID[] result = new AID[descriptions.length];
        Arrays.stream(descriptions).map(DFAgentDescription::getName).collect(Collectors.toList()).toArray(result);
        return result;
    }

    public static void log(String text){
        System.out.println(text);
    }
}
