package TestProblem.State;

import aima.search.framework.Successor;

import java.util.ArrayList;
import java.util.List;

public class SuccessorGenerator {

    private RandomSelector modifiers;
    public double seed;

    public static List createSuccessor(Object o) {
        State state = ((State) o);
        state.sync();

        if(state.successorGenerator == null) {
            state.successorGenerator = new SuccessorGenerator();
        }

        List<Successor> successors = new ArrayList<>();

        State child = state.generateSuccessor();
        if(child != null) {
            successors.add(new Successor("", child));
        } else {
//            System.out.println("No child found");
        }

        return successors;
    }

    private SuccessorGenerator() {
        seed = Math.random();
        List<StateModifier> modifiers = new ArrayList<>();

        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::increaseTop));
        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::increaseBottom));
        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::increaseRight));
        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::increaseLeft));

        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::decreaseTop));
        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::decreaseBottom));
        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::decreaseRight));
        modifiers.add(new StateSlicesModifier(0.25*0.25, Slice::decreaseLeft));

        modifiers.add(new StateSlicesModifier(0.25, Slice::removeSlice));

        modifiers.add(new StateSliceCreator(0.25));

        this.modifiers = new RandomSelector<>(modifiers);
    }

    public State generateSuccessor(State state) {

        StateModifier modifier = (StateModifier) modifiers.popRandom();
        if(modifier == null) {
            return null;
        }

        ChangeLog log = modifier.modify(seed);

        if(log.was_possible) {
            State child = state.shadow_copy();

            int new_area = log.modified_area.getArea();
            if(!log.becomes_used) {
                new_area = -new_area;
            }

            child.area += new_area;
            child.changeLog = log;

            return child;
        } else {
            return null;
        }
    }
}
