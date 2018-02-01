package TestProblem.State;

import aima.search.framework.HeuristicFunction;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class State {

    private int area;
    private ArrayList<Slice> slices;
    private boolean is_synced;

    public static PizzaLayout pizza;
    private Slice.ChangeLog changeLog;


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
                    slice.writeIntoPizza(true);
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
            changeLog.apply();
        }
        is_synced = true;
    }

    private State deep_copy() {
        State state = new State();
        state.area = area;
        state.is_synced = is_synced;
        state.changeLog = null;
        for(Slice slice : slices) {
            state.slices.add(slice.deep_copy());
        }
        return state;
    }

    public ArrayList<State> generateSuccessors() {
        ArrayList<State> successors = new ArrayList<>();

        State child = this.deep_copy();
        for(int i = 0; i < slices.size(); ++i) {
            Slice slice = child.slices.get(i);
            Slice.ChangeLog log = slice.increaseTop();
            if(log.is_possible) {
                child.changeLog = log;
                child.area += log.slice.getArea();
                successors.add(child);
                child = this.deep_copy();
            }
        }
        return successors;
    }

    public String toString() {
        String s = slices.size() + "\n";
        for(Slice slice : slices) {
            s += slice.toString() + "\n";
        }
        return s;
    }

    public static class SuccessorsGenerator implements SuccessorFunction {

        @Override
        public List getSuccessors(Object o) {
            System.out.println("Generating successors");
            State state = ((State) o);
            state.sync();

            List<Successor> successors = new ArrayList<>();
            for(State child : state.generateSuccessors()) {
                System.out.println(child.area);
                successors.add(new Successor("", child));
            }
            return successors;
        }
    }

    public static class HeuristicCalculator implements HeuristicFunction {

        @Override
        public double getHeuristicValue(Object state) {
            return -((State) state).getArea();
        }
    }

}
