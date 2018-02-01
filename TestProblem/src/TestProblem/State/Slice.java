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


    public Slice(int x, int y) {

        r1 = x;
        r2 = x;

        c1 = y;
        c2 = y;

        n_M = State.pizza.layout[x][y].ingredient == PizzaLayout.Ingredient.M ? 1 : 0;
        n_T = 1 - n_M;
    }

    public Slice(int r1, int r2, int c1, int c2) {
        this.r1 = r1;
        this.r2 = r2;
        this.c1 = c1;
        this.c2 = c2;

        n_M = -1;
        n_T = -1;
    }

    public int getArea() {
        return (r2 - r1 + 1) * (c2 - c1 + 1);
    }

    public void expand() {
        if(!State.pizza.getInfoArea(r1, r2, c1, c2).is_free) {
            return;
        }
        boolean can_top = true;
        boolean can_bottom = true;
        boolean can_right = true;
        boolean can_left = true;
        while(can_top || can_bottom || can_right || can_left) {
            // TODO try to change order and experiment results
            if(can_top) {
                can_top = increaseTop().was_possible;
            }
            if(can_left) {
                can_left = increaseLeft().was_possible;
            }
            if(can_right) {
                can_right = increaseRight().was_possible;
            }
            if(can_bottom) {
                can_bottom = increaseBottom().was_possible;
            }
        }
    }

    public boolean isValid() {
        assert getArea() == n_M + n_T;
        return n_T >= MIN_AREA && n_M >= MIN_AREA && n_T + n_M <= MAX_AREA;
    }

    public void writeIntoPizza(boolean used) {
        assert isValid();
        State.pizza.setUsedArea(r1, r2, c1, c2, used);
    }

    private ChangeLog increaseBorder(int r1, int r2, int c1, int c2, boolean vertical, DimensionsModifier modifier) {
        ChangeLog log = new ChangeLog(new Slice(r1, r2, c1, c2), true);

        int incr_area = vertical ? (c2 - c1 + 1) : (r2 - r1 + 1);
        if(n_T + n_M + incr_area > MAX_AREA) {
            log.was_possible = false;
            return log;
        }
        PizzaLayout.InfoArea info = State.pizza.getInfoArea(r1, r2, c1, c2);
        if(info.is_free) {
            n_T += info.n_T;
            n_M += info.n_M;

            log.was_possible = true;
            modifier.modifyDimension();

            return log;
        } else {
            log.was_possible = false;
            return log;
        }
    }

    public ChangeLog increaseTop() {
        return increaseBorder(r1 - 1, r1 - 1, c1, c2, true, () -> --r1);
    }

    public ChangeLog increaseBottom() {
        return increaseBorder(r2 + 1, r2 + 1, c1, c2, true, () -> ++r2);
    }

    public ChangeLog increaseRight() {
        return increaseBorder(r1, r2, c2 + 1, c2 + 1, false, () -> ++c2);
    }

    public ChangeLog increaseLeft() {
        return increaseBorder(r1, r2, c1 - 1, c1 - 1, false, () -> --c1);
    }

    private ChangeLog decreaseBorder(int r1, int r2, int c1, int c2, DimensionsModifier modifier) {
        ChangeLog log = new ChangeLog(new Slice(r1, r2, c1, c2), false);

        PizzaLayout.InfoArea info = State.pizza.getInfoArea(r1, r2, c1, c2);
        if(n_T - info.n_T >= MIN_AREA && n_M - info.n_M >= MIN_AREA) {
            n_T -= info.n_T;
            n_M -= info.n_M;

            log.was_possible = true;
            modifier.modifyDimension();

            return log;
        } else {
            log.was_possible = false;
            return log;
        }
    }

    public interface  DimensionsModifier {
        void modifyDimension();
    }

    public ChangeLog decreaseTop() {
        return decreaseBorder(r1, r1, c1, c2, () -> ++r1);
    }

    public ChangeLog decreaseBottom() {
        return decreaseBorder(r2, r2, c1, c2, () -> --r2);
    }

    public ChangeLog decreaseRight() {
        return decreaseBorder(r1, r2, c2, c2, () -> --c2);
    }

    public ChangeLog decreaseLeft() {
        return decreaseBorder(r1, r2, c1, c1, () -> ++c1);
    }

    public ChangeLog removeSlice() {
        ChangeLog log = new ChangeLog(this, false);
        log.was_possible = true;
        return log;
    }

    public String toString() {
        return r1 + " " + r2 + " " + c1 + " " + c2;
    }

    public Slice deep_copy() {
        Slice slice = new Slice(r1, r2, c1, c2);
        slice.n_M = n_M;
        slice.n_T = n_T;
        return slice;
    }

    public class ChangeLog {
        public Slice slice;
        boolean was_possible;
        boolean becomes_used;

        public ChangeLog(Slice slice, boolean becomes_used) {
            this.slice = slice;
            this.becomes_used = becomes_used;
        }

        public void apply() {
            slice.writeIntoPizza(becomes_used);
        }
    }
}