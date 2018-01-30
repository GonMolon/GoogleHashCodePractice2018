package TestProblem.State;

import java.util.ArrayList;
import java.util.stream.Stream;

public class PizzaLayout {

    protected static int R;
    protected static int C;
    protected static int L;
    protected static int H;

    public Cell[][] layout;

    public PizzaLayout(Stream<String> input) {
        ArrayList<String> lines = new ArrayList<>();
        input.forEach(lines::add);

        String[] dimensions = lines.remove(0).split("\\s+");
        R = Integer.parseInt(dimensions[0]);
        C = Integer.parseInt(dimensions[1]);
        L = Integer.parseInt(dimensions[2]);
        H = Integer.parseInt(dimensions[3]);

        layout = new Cell[R][C];
        for(int i = 0; i < R; ++i) {
            for(int j = 0; j < C; ++j) {
                char c = lines.get(i).charAt(j);
                Ingredient ingredient = c == 'M' ? Ingredient.M : Ingredient.T;
                layout[i][j] = new Cell(ingredient, false);
            }
        }
    }

    public void to_string() {
        for(int i = 0; i < R; ++i) {
            for(int j = 0; j < C; ++j) {
                Cell c = layout[i][j];
                if(c.used) {
                    System.out.print('.');
                } else {
                    System.out.print(c.ingredient);
                }
            }
            System.out.print('\n');
        }
    }

    public class Cell {
        public Ingredient ingredient;
        public boolean used;

        public Cell(Ingredient ingredient, boolean used) {
            this.ingredient = ingredient;
            this.used = used;
        }
    }

    public enum Ingredient {
        T, M
    }
}
