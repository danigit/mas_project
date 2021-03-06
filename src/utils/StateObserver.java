package utils;

import interfaces.HomeAutomation;
import jade.lang.acl.ACLMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;

/**
 * Class that allows to notify the undersigned's that a state has changed
 * @param <T> - the state that has changed his value
 * @param <U> - the undersigned's to notify
 */
public class StateObserver<T, U> implements Observer, HomeAutomation {
    private T state;
    private U subscriptions;

    public StateObserver(U subscriptions){
        state = null;
        this.subscriptions = subscriptions;
    }

    @Override
    public void update(Observable o, Object arg) { ;
        if (arg != null){
            state = (T) arg;
            if (state.toString().equals(BROKEN)){
                ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                inform.setContent(state.toString());
                Method method = null;
                try {
                    method = subscriptions.getClass().getMethod("notifyAgents", ACLMessage.class);
                    method.invoke(subscriptions, inform);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }else {
            Util.log("The argument is null");
        }
    }
}
