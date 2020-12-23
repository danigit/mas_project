import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

/**
 * Class that implements static methods that are useful to the agents
 */
public class Util {

    public static Logger logger = null;

    /**
     * Method that register the services of and agent with the yellow pages
     * @param agent - the agent that offers the services
     * @param serviceType - the type of the service to be registered
     * @param serviceName - the name of the service to be registered
     */
    public static void registerService(Agent agent, String serviceType, String serviceName){
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(agent.getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType);
        serviceDescription.setName(serviceName);
        agentDescription.addServices(serviceDescription);

        try{
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
        return DFService.createSubscriptionMessage(agent, defaultDF, agentDescription, null);

    }

    public static void log(String text){
        System.out.println(text);
    }
}

// if we want to deregister an agent we do it in takeDown