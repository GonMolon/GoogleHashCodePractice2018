package TestProblem;

import TestProblem.State.State;
import aima.search.framework.SuccessorFunction;

import java.util.List;

public class SuccessorsGenerator implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        State state = ((State) o);
        state.sync();

        return null;
    }
}
