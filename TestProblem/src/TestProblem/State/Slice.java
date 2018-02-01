package TestProblem.State;

public class Slice {

    public static int MAX_AREA;
    public static int MIN_AREA;

    public int id;

    public int r1;
    public int r2;
    public int c1;
    public int c2;

    public int n_T;
    public int n_M;

    private Slice(int id, int x, int y) {

        this.id = id;

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

    public int getId() {
        return id;
    }

    public boolean isValid() {
        assert getArea() == n_M + n_T;
        return n_T >= MIN_AREA && n_M >= MIN_AREA && n_T + n_M <= MAX_AREA;
    }

    public void writeIntoPizza(boolean used) {
        State.pizza.setUsedArea(r1, r2, c1, c2, used);
    }

    private ChangeLog increaseBorder(int r1, int r2, int c1, int c2, boolean vertical, DimensionsModifier modifier) {

        ChangeLog log = new ChangeLog(true);

        int incr_area = vertical ? (c2 - c1 + 1) : (r2 - r1 + 1);
        if(n_T + n_M + incr_area > MAX_AREA) {
            log.was_possible = false;
            return log;
        }

        PizzaLayout.InfoArea info = State.pizza.getInfoArea(r1, r2, c1, c2);
        if(info.is_free) {

            log.was_possible = true;
            log.modified_area = new Slice(r1, r2, c1, c2);
            log.remaining_area = deep_copy();
            log.remaining_area.n_T += info.n_T;
            log.remaining_area.n_M += info.n_M;
            modifier.modifyDimension(log.remaining_area);

            return log;
        } else {
            log.was_possible = false;
            return log;
        }
    }

    public ChangeLog increaseTop() {
        return increaseBorder(r1 - 1, r1 - 1, c1, c2, true, slice -> slice.r1--);
    }

    public ChangeLog increaseBottom() {
        return increaseBorder(r2 + 1, r2 + 1, c1, c2, true, slice -> slice.r2++);
    }

    public ChangeLog increaseRight() {
        return increaseBorder(r1, r2, c2 + 1, c2 + 1, false, slice -> slice.c2++);
    }

    public ChangeLog increaseLeft() {
        return increaseBorder(r1, r2, c1 - 1, c1 - 1, false, slice -> slice.c1--);
    }

    private ChangeLog decreaseBorder(int r1, int r2, int c1, int c2, DimensionsModifier modifier) {

        ChangeLog log = new ChangeLog(false);

        PizzaLayout.InfoArea info = State.pizza.getInfoArea(r1, r2, c1, c2);
        if(n_T - info.n_T >= MIN_AREA && n_M - info.n_M >= MIN_AREA) {

            log.was_possible = true;
            log.modified_area = new Slice(r1, r2, c1, c2);
            log.remaining_area = deep_copy();
            log.remaining_area.n_T -= info.n_T;
            log.remaining_area.n_M -= info.n_M;
            modifier.modifyDimension(log.remaining_area);

            return log;
        } else {
            log.was_possible = false;
            return log;
        }
    }

    public ChangeLog decreaseTop() {
        return decreaseBorder(r1, r1, c1, c2, slice -> slice.r1++);
    }

    public ChangeLog decreaseBottom() {
        return decreaseBorder(r2, r2, c1, c2, slice -> slice.r2--);
    }

    public ChangeLog decreaseRight() {
        return decreaseBorder(r1, r2, c2, c2, slice -> slice.c2--);
    }

    public ChangeLog decreaseLeft() {
        return decreaseBorder(r1, r2, c1, c1, slice -> slice.c1++);
    }

    public ChangeLog removeSlice() {
        ChangeLog log = new ChangeLog(false);
        log.modified_area = this;
        log.remaining_area = new Slice(-1, 0, -1, 0);
        log.was_possible = true;
        return log;
    }

    public static ChangeLog createSlice(int id, int x, int y) {
        Slice slice = new Slice(id, x, y);
        ChangeLog log = new ChangeLog(true);

        if(!State.pizza.getInfoArea(slice.r1, slice.r2, slice.c1, slice.c2).is_free) {
            log.was_possible = false;
            return log;
        }

        boolean can_top = true;
        boolean can_bottom = true;
        boolean can_right = true;
        boolean can_left = true;

        while(can_top || can_bottom || can_right || can_left) {
            // TODO try to change order and experiment results
            if(can_top) {
                log = slice.increaseTop();
                can_top = log.was_possible;
                if(can_top) {
                    slice = log.remaining_area;
                }
            }
            if(can_left) {
                log = slice.increaseLeft();
                can_left = log.was_possible;
                if(can_left) {
                    slice = log.remaining_area;
                }
            }
            if(can_right) {
                log = slice.increaseRight();
                can_right = log.was_possible;
                if(can_right) {
                    slice = log.remaining_area;
                }
            }
            if(can_bottom) {
                log = slice.increaseBottom();
                can_bottom = log.was_possible;
                if(can_bottom) {
                    slice = log.remaining_area;
                }
            }
        }

        log.was_possible = slice.isValid();
        if(log.was_possible) {
            log.modified_area = slice;
            log.remaining_area = slice;
        }

        return log;
    }

    public interface  DimensionsModifier {
        void modifyDimension(Slice slice);
    }

    public String toString() {
        return r1 + " " + r2 + " " + c1 + " " + c2;
    }

    public Slice deep_copy() {
        Slice slice = new Slice(r1, r2, c1, c2);
        slice.id = id;
        slice.n_M = n_M;
        slice.n_T = n_T;
        return slice;
    }

}