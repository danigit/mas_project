import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class Util {

    public static void registerService(Agent agent, AID aid, String serviceType, String serviceName){
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);

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
}
