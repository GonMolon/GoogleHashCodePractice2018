package TestProblem.State;

import aima.search.framework.HeuristicFunction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class State {

    public static PizzaLayout pizza;
    public static int lastId = 0;
    public static HashMap<Integer, Slice> slices;
    public static ArrayList<Slice> best_solution;
    public static int best_area;
    public static ArrayList<Integer> history;
    public static ArrayList<Integer> iterations;

    public int area;
    private boolean is_synced;

    public ChangeLog changeLog;
    public SuccessorGenerator successorGenerator;


    public static State createInitialState(Stream<String> input) {

        pizza = new PizzaLayout(input);
        slices = new HashMap<>();
        best_area = -1;
        best_solution = null;
        history = new ArrayList<>();
        iterations = new ArrayList<>();

        State state = new State();
        state.createInitialSlices();
//        state.is_synced = true;

        return state;
    }

    private State() {
        is_synced = false;
        changeLog = null;
        successorGenerator = null;
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

    public void sync() {
        if(!is_synced) {
            if(changeLog != null) {
                changeLog.apply();
            }
        }
        is_synced = true;

        history.add((int)getArea());
        iterations.add(iterations.size()+1);
        System.out.println(iterations.size());

        if(area > best_area) {
            best_solution = new ArrayList<>();
            slices.values().forEach(slice -> best_solution.add(slice.deep_copy()));
            best_area = area;
        }
    }

    public State shadow_copy() {
        State state = new State();
        state.area = area;
        state.is_synced = false;
        state.changeLog = null;
        return state;
    }

    public State generateSuccessor() {
        return successorGenerator.generateSuccessor(this);
    }

    public static void printBestSolution(PrintWriter output) {
        output.println(slices.size());
        for(Slice slice : slices.values()) {
            output.println(slice.toString());
        }
    }

    public static class HeuristicCalculator implements HeuristicFunction {

        @Override
        public double getHeuristicValue(Object state) {
            return -((State) state).getArea()/pizza.getArea();
        }
    }

}
