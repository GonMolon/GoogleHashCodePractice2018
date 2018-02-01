package TestProblem.State;

import aima.search.framework.HeuristicFunction;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class State {

    public static PizzaLayout pizza;
    public static int lastId = 0;
    public static HashMap<Integer, Slice> slices;

    private int area;
    private boolean is_synced;

    private ChangeLog changeLog;


    public static State createInitialState(Stream<String> input) {

        pizza = new PizzaLayout(input);
        slices = new HashMap<>();

        State state = new State();
        state.createInitialSlices();
        state.is_synced = true;

        return state;
    }

    private State() {
        is_synced = false;
        area = 0;
    }

    private void createInitialSlices() {
        System.out.println("Creating initial slices");
        for(int i = 0; i < pizza.R; ++i) {
            for(int j = 0; j < pizza.C; ++j) {
                ChangeLog log = Slice.createSlice(lastId, i, j);
                if(log.was_possible) {
                    log.apply();
                    area += log.remaining_area.getArea();
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

    private State child = null;

    private ArrayList<State> generateSuccessors() {
        ArrayList<State> successors = new ArrayList<>();

        child = shadow_copy();
        for(int id : slices.keySet()) {
            generateSuccessor(id, slice -> slice.increaseTop(), successors);
            generateSuccessor(id, slice -> slice.increaseBottom(), successors);
            generateSuccessor(id, slice -> slice.increaseRight(), successors);
            generateSuccessor(id, slice -> slice.increaseLeft(), successors);
            generateSuccessor(id, slice -> slice.decreaseTop(), successors);
            generateSuccessor(id, slice -> slice.decreaseBottom(), successors);
            generateSuccessor(id, slice -> slice.decreaseRight(), successors);
            generateSuccessor(id, slice -> slice.decreaseLeft(), successors);
        }
        child = null;
        return successors;
    }

    private void generateSuccessor(int id, SliceModifier modifier, ArrayList<State> successors) {
        Slice slice = child.slices.get(id);

        ChangeLog log = modifier.modify(slice);

        if(log.was_possible) {
            int new_area = log.modified_area.getArea();
            if(!log.becomes_used) {
                new_area = -new_area;
            }
            child.area += new_area;
            child.changeLog = log;

            successors.add(child);
            child = shadow_copy();
        }
    }

    private State shadow_copy() {
        State state = new State();
        state.area = area;
        state.is_synced = false;
        state.changeLog = null;
        return state;
    }

    private interface SliceModifier {
        ChangeLog modify(Slice slice);
    }

    public String toString() {
        String s = slices.size() + "\n";
        for(Slice slice : slices.values()) {
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
