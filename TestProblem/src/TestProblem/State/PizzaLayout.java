package TestProblem.State;

import java.util.stream.Stream;

public class PizzaLayout {

    protected static int R;
    protected static int C;
    protected static int L;
    protected static int H;



    public PizzaLayout(Stream<String> input) {
        String[] dimensions = input.findFirst().get().split("\\s+");
        R = Integer.parseInt(dimensions[0]);
        C = Integer.parseInt(dimensions[1]);
        L = Integer.parseInt(dimensions[2]);
        H = Integer.parseInt(dimensions[3]);


    }

}
