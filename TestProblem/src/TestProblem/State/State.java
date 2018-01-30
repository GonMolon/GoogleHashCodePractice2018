package TestProblem.State;

import java.util.stream.Stream;

public class State {

    private int area;
    private int size;
    private boolean is_synced;


    public static State createInitialState(Stream<String> input) {

        PizzaLayout layout = new PizzaLayout(input);
        State state = new State();

        state.is_synced = true;
        return state;
    }

    private State() {
        is_synced = false;
        size = 0;
        area = 0;
    }

    public double getArea() {
        return area;
    }

    public int getNumSlices() {
        return size;
    }

    public void sync() {
        is_synced = true;
    }
}
