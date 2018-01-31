package TestProblem.State;

import java.util.ArrayList;
import java.util.stream.Stream;

public class PizzaLayout {

    public static int R;
    public static int C;

    public Cell[][] layout;

    public PizzaLayout(Stream<String> input) {
        ArrayList<String> lines = new ArrayList<>();
        input.forEach(lines::add);

        String[] dimensions = lines.remove(0).split("\\s+");
        R = Integer.parseInt(dimensions[0]);
        C = Integer.parseInt(dimensions[1]);
        Slice.MIN_AREA = Integer.parseInt(dimensions[2]);
        Slice.MAX_AREA = Integer.parseInt(dimensions[3]);

        layout = new Cell[R][C];
        for(int i = 0; i < R; ++i) {
            for(int j = 0; j < C; ++j) {
                char c = lines.get(i).charAt(j);
                Ingredient ingredient = c == 'M' ? Ingredient.M : Ingredient.T;
                layout[i][j] = new Cell(ingredient, false);
            }
        }
    }

    public InfoArea getInfoArea(int r1, int r2, int c1, int c2) {
        // TODO remove this safety check
        assert r1 <= r2 && c1 <= c2;

        InfoArea info = new InfoArea();
        info.area = (r2 - r1) * (c2 - c1);

        if(r1 < 0 || r2 >= R || c1 < 0 || c2 >= C) {
            info.is_free = false;
            return info;
        }

        for(int i = r1; i <= r2; ++i) {
            for(int j = c1; j <= c2; ++j) {
                Cell cell = layout[i][j];
                if(cell.used) {
                    info.is_free = false;
                }
                if(cell.ingredient == Ingredient.M) {
                    info.n_M++;
                } else {
                    info.n_T++;
                }
            }
        }

        return info;
    }

    public double getArea() {
        return R * C;
    }

    public class InfoArea {
        public int n_T;
        public int n_M;
        public int area;
        public boolean is_free;

        InfoArea() {
            n_T = 0;
            n_M = 0;
            area = 0;
            is_free = true;
        }
    }

    public void setUsedArea(int r1, int r2, int c1, int c2, boolean used) {
        for(int i = r1; i <= r2; ++i) {
            for (int j = c1; j <= c2; ++j) {
                layout[i][j].used = used;
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

    public void to_string(ArrayList<Slice> slices) {
        char[][] output = new char[R][C];
        for(int i = 0; i < R; ++i) {
            for(int j = 0; j < C; ++j) {
                Ingredient c = layout[i][j].ingredient;
                output[i][j] = c.toString().charAt(0);
            }
        }
        for(int i = 0; i < slices.size(); ++i) {
            Slice slice = slices.get(i);
            for(int r = slice.r1; r <= slice.r2; ++r) {
                for(int c = slice.c1; c <= slice.c2; ++c) {
                    String s = String.valueOf(i);
                    output[r][c] = s.charAt(s.length() - 1);
                }
            }
        }
        for(int i = 0; i < R; ++i) {
            for(int j = 0; j < C; ++j) {
                System.out.print(output[i][j]);
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
