package utils;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;

import interfaces.HomeAutomation;

import java.util.Vector;

public class Responder extends SubscriptionResponder {
    private final SubscriptionManager subscriptionManager;
    private final Agent agent;

    public Responder(Agent agent, MessageTemplate messageTemplate, SubscriptionManager subscriptionManager) {
        super(agent, messageTemplate, subscriptionManager);

        this.subscriptionManager = subscriptionManager;
        this.agent = agent;
    }

    @Override
    protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
        if (subscription.getContent().equals(HomeAutomation.BROKEN)) {
            Util.log("Accepting subscription to: " + agent.getLocalName());
            this.subscriptionManager.register(createSubscription(subscription));
        }
        return null;
    }

    public void notifyAgents(ACLMessage informMessage) {
        Vector subscriptions = getSubscriptions();
        Util.log("Getting the subscriptions for " + agent.getLocalName() + " which has " + subscriptions.size() + " subscriptions");
        for (int i = 0; i < subscriptions.size(); i++) {
            Util.log("Informing the agent: " + subscriptions.elementAt(i).getClass().getName());
            ((SubscriptionResponder.Subscription) subscriptions.elementAt(i)).notify(informMessage);
        }
    }
}