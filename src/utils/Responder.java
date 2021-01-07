package utils;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;

import interfaces.HomeAutomation;

import java.util.Vector;

/**
 * Class that handles the subscription requests
 */
public class Responder extends SubscriptionResponder {
    private final SubscriptionManager subscriptionManager;

    public Responder(Agent agent, MessageTemplate messageTemplate, SubscriptionManager subscriptionManager) {
        super(agent, messageTemplate, subscriptionManager);
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
        if (subscription.getContent().equals(HomeAutomation.BROKEN)) {
            this.subscriptionManager.register(createSubscription(subscription));
        }
        return null;
    }

    public void notifyAgents(ACLMessage informMessage) {
        Vector subscriptions = getSubscriptions();
        for (int i = 0; i < subscriptions.size(); i++) {
            ((SubscriptionResponder.Subscription) subscriptions.elementAt(i)).notify(informMessage);
        }
    }
}