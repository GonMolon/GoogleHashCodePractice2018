package TestProblem.State;

public class ChangeLog {
    public Slice modified_area;
    public Slice remaining_area;

    boolean was_possible;
    boolean becomes_used;

    public ChangeLog(boolean becomes_used) {
        modified_area = null;
        remaining_area = null;
        was_possible = false;
        this.becomes_used = becomes_used;
    }

    public void apply() {
        assert was_possible;
        modified_area.writeIntoPizza(becomes_used);
        if(remaining_area.getArea() == 0) {
            State.slices.remove(remaining_area.getId());
        } else if(State.slices.containsKey(remaining_area.getId())) {
            State.slices.replace(remaining_area.getId(), remaining_area);
        } else {
            State.slices.put(remaining_area.getId(), remaining_area);
            State.lastId++;
        }
    }
}
