package TestProblem.State;

public class Slice {

    public static int MAX_AREA;
    public static int MIN_AREA;

    public int r1;
    public int r2;
    public int c1;
    public int c2;

    public int n_T;
    public int n_M;

    private boolean is_created;

    public Slice(int x, int y) {
        n_T = 0;
        n_M = 0;

        r1 = x;
        r2 = x;

        c1 = y;
        c2 = y;

        n_M = State.pizza.layout[x][y].ingredient == PizzaLayout.Ingredient.M ? 1 : 0;
        n_T = 1 - n_M;

        is_created = false;
    }

    public int getArea() {
        return (r2 - r1) * (c2 - c1);
    }

    public boolean isValid() {
        assert (r2 - r1) * (c2 - c1) == n_M + n_T;
        boolean is_valid = n_T >= MIN_AREA && n_M >= MIN_AREA && n_T + n_M <= MAX_AREA;

        if(is_valid && !is_created) {
            is_created = true;
            State.pizza.setUsedArea(r1, r2, c1, c2, true);
        }

        return is_valid;
    }

    private boolean increaseBorder(int r1, int r2, int c1, int c2) {
        if(n_T + n_M + (c2 - c1) > MAX_AREA) {
            return false;
        }
        PizzaLayout.InfoArea info = State.pizza.getInfoArea(r1, r2, c1, c2);
        if(info.is_free) {
            n_T += info.n_T;
            n_M += info.n_M;

            if(is_created) {
                State.pizza.setUsedArea(r1, r2, c1, c2, true);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean increaseTop() {
        boolean done = increaseBorder(r1 - 1, r1 - 1, c1, c2);
        if(done) {
            --r1;
        }
        return done;
    }

    public boolean increaseBottom() {
        boolean done = increaseBorder(r2 + 1, r2 + 1, c1, c2);
        if(done) {
            ++r2;
        }
        return done;
    }

    public boolean increaseRight() {
        boolean done = increaseBorder(r1, r2, c2 + 1, c2 + 1);
        if(done) {
            ++c2;
        }
        return done;
    }

    public boolean increaseLeft() {
        boolean done = increaseBorder(r1, r2, c1 - 1, c1 - 1);
        if(done) {
            --c1;
        }
        return done;
    }

    private boolean decraseBorder(int r1, int r2, int c1, int c2) {
        assert is_created;

        PizzaLayout.InfoArea info = State.pizza.getInfoArea(r1, r2, c1, c2);
        if(n_T - info.n_T >= MIN_AREA && n_M - info.n_M >= MIN_AREA) {
            n_T -= info.n_T;
            n_M -= info.n_M;

            State.pizza.setUsedArea(r1, r2, c1, c2, false);

            return true;
        } else {
            return false;
        }
    }

    public boolean decreaseTop() {
        boolean done = decraseBorder(r1, r1, c1, c2);
        if(done) {
            ++r1;
        }
        return done;
    }

    public boolean decraseBottom() {
        boolean done = decraseBorder(r2, r2, c1, c2);
        if(done) {
            --r2;
        }
        return done;
    }

    public boolean decreaseRight() {
        boolean done = decraseBorder(r1, r2, c2, c2);
        if(done) {
            --c2;
        }
        return done;
    }

    public boolean decraseLeft() {
        boolean done = decraseBorder(1, r2, c1, c1);
        if(done) {
            ++c1;
        }
        return done;
    }

    public void removeSlice() {
        State.pizza.setUsedArea(r1, r2, c1, c2, false);
    }
}