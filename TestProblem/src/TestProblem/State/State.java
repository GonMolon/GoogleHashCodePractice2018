package TestProblem.State;

import java.util.ArrayList;
import java.util.stream.Stream;

public class State {

    private int area;
    private ArrayList<Slice> slices;
    private boolean is_synced;

    public static PizzaLayout pizza;


    public static State createInitialState(Stream<String> input) {

        pizza = new PizzaLayout(input);
        State state = new State();
        state.createInitialSlices((int) (Math.random() * pizza.R));

        state.is_synced = true;
        return state;
    }

    private State() {
        is_synced = false;
        slices = new ArrayList<>();
        area = 0;
    }

    private void createInitialSlices(int first_row) {
        System.out.println("Creating initial slices");
        for(int i = 0; i < pizza.R; ++i) {
            for(int j = 0; j < pizza.C; ++j) {
                Slice slice = new Slice(i, j);
                slice.expand();
                if(slice.isValid()) {
                    slices.add(slice);
                    area += slice.getArea();
                }
            }
        }
        System.out.println("Initial area: " + area);
    }

    public double getArea() {
        return area;
    }

    public int getNumSlices() {
        return slices.size();
    }

    public void sync() {
        if(!is_synced) {
//            Apply modification of construction of this state into pizza layout
        }
        is_synced = true;
    }

    public String toString() {
        String s = slices.size() + "\n";
        for(Slice slice : slices) {
            s += slice.toString() + "\n";
        }
        return s;
    }

}
