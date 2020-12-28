package utils;

import java.util.Observable;

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
