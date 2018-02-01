package TestProblem;

import TestProblem.State.State;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Main {

    private final static int iterations = 1000;
    private final static int stiter = 1;
    private final static int k = 100;
    private final static double lamda = 0.005;

    public static void main(String[] args) {

        ArrayList<File> files = new ArrayList<>();
        files.add(new File("TestProblem/test/example.in"));
        files.add(new File("TestProblem/test/small.in"));
        files.add(new File("TestProblem/test/medium.in"));
//        files.add(new File("TestProblem/test/big.in"));

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

            Problem problem = new Problem(initial_state, new State.SuccessorsGenerator(), o -> false, new State.HeuristicCalculator());
            Search search = new SimulatedAnnealingSearch(iterations, stiter, k, lamda);

            System.out.println("Starting local search using algorithm: " + search.getClass().getName());
            long start_time = System.currentTimeMillis();
            try {
                SearchAgent agent = new SearchAgent(problem, search);
                long total_time = System.currentTimeMillis() - start_time;

                State final_state = ((State)search.getGoalState());
                final_state.sync();
                System.out.println("Area: " + final_state.getArea());
                System.out.println("Success ratio: " + final_state.getArea() / State.pizza.getArea());
                System.out.println("Num of slices: " + State.best_solution.size());
                System.out.println("Execution time: " + total_time);
                System.out.println("-----------------------------");

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                PrintWriter output = new PrintWriter(f.getPath().replace(".in", ".out"), "UTF-8");
                State.printBestSolution(output);
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
