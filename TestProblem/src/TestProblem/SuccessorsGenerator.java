package TestProblem;

import TestProblem.State.State;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class SuccessorsGenerator implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        State state = ((State) o);
        state.sync();
        List<State> successors = new ArrayList<>();

        return successors;
    }
}
