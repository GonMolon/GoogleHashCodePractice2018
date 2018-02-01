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
        state.createInitialSlices();

        state.is_synced = true;
        return state;
    }

    private State() {
        is_synced = false;
        slices = new ArrayList<>();
        area = 0;
    }

    private void createInitialSlices() {
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

    private State child = null;

    private ArrayList<State> generateSuccessors() {
        ArrayList<State> successors = new ArrayList<>();

        child = this.deep_copy();
        for(int i = 0; i < slices.size(); ++i) {
            generateSuccessor(i, slice -> slice.increaseTop(), successors);
            generateSuccessor(i, slice -> slice.increaseBottom(), successors);
            generateSuccessor(i, slice -> slice.increaseRight(), successors);
            generateSuccessor(i, slice -> slice.increaseLeft(), successors);
            generateSuccessor(i, slice -> slice.decreaseTop(), successors);
            generateSuccessor(i, slice -> slice.decreaseBottom(), successors);
            generateSuccessor(i, slice -> slice.decreaseRight(), successors);
            generateSuccessor(i, slice -> slice.decreaseLeft(), successors);
        }
        child = null;
        return successors;
    }

    private void generateSuccessor(int slice_id, SliceModifier modifier, ArrayList<State> successors) {
        Slice slice = child.slices.get(slice_id);

        Slice.ChangeLog log = modifier.modify(slice);

        if(log.was_possible) {
            int new_area = log.slice.getArea();
            if(!log.becomes_used) {
                new_area = -new_area;
            }
            child.area += new_area;
            child.changeLog = log;

            successors.add(child);
            child = this.deep_copy();
        }
    }

    private interface SliceModifier {
        Slice.ChangeLog modify(Slice slice);
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
                successors.add(new Successor("", child));
            }

            System.out.println(successors.size() + " successors generated");

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
