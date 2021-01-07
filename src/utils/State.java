package utils;

import java.util.Observable;

/**
 * Class that define a state on which we can register for changes
 * @param <T> - the type of the state
 */
public class State<T> extends Observable {
    private T state;

    public State(T state){
        this.state = state;
    }

    public T getValue(){
        return this.state;
    }

    public void setValue(T state) {
        this.state = state;
        setChanged();
        notifyObservers(this.state);
    }
}
