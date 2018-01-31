package TestProblem;

import TestProblem.State.State;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        ArrayList<File> files = new ArrayList<>();
        files.add(new File("TestProblem/test/example.in"));
        files.add(new File("TestProblem/test/small.in"));
        files.add(new File("TestProblem/test/medium.in"));
        files.add(new File("TestProblem/test/big.in"));

        for(File f : files) {

            System.out.println("Running file: " + f.getPath());

            Stream<String> input = null;
            try {
                input = Files.lines(Paths.get(f.getPath()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            State initial_state = State.createInitialState(input);

            Problem problem = new Problem(initial_state, new SuccessorsGenerator(), o -> false, new HeuristicCalculator());

            State best_solution = null;
            for(Search search : new Search[]{new HillClimbingSearch()}) {
                System.out.println("Starting local search using algorithm: " + search.getClass().getName());
                long start_time = System.currentTimeMillis();
                try {
                    SearchAgent agent = new SearchAgent(problem, search);
                    long total_time = System.currentTimeMillis() - start_time;

                    State final_state = ((State)search.getGoalState());
                    System.out.println("Area: " + final_state.getArea());
                    System.out.println("Success ratio: " + final_state.getArea() / State.pizza.getArea());
                    System.out.println("Num of slices: " + final_state.getNumSlices());
                    System.out.println("Execution time: " + total_time);
                    System.out.println("-----------------------------");

                    if(best_solution == null || best_solution.getArea() < final_state.getArea()) {
                        best_solution = final_state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                PrintWriter output = new PrintWriter(f.getPath().replace(".in", ".out"), "UTF-8");
                output.println(best_solution.toString());
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
