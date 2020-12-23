import java.util.Observable;

public class State<T> extends Observable {
    private T state;

    public State(T state){
        this.state = state;
    }

    public T getDoorState(){
        return this.state;
    }

    public void setDoorState(T state) {
        this.state = state;
        Util.log("Updating the state to: " + this.state.toString());
        setChanged();
        notifyObservers(this.state);
    }
}
