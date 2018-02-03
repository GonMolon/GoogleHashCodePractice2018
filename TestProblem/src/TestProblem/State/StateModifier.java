package TestProblem.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

abstract class StateModifier implements RandomSelector.ProbabilityItem {

    double probability;

    public StateModifier(double probability) {
        this.probability = probability;
    }

    @Override
    public double getProbability() {
        return probability;
    }

    public abstract ChangeLog modify(double seed);

}

class StateSlicesModifier extends StateModifier {

    SliceModifier modifier;

    public StateSlicesModifier(double probability, SliceModifier modifier) {
        super(probability);
        this.modifier = modifier;
    }

    @Override
    public ChangeLog modify(double seed) {
        ArrayList<Integer> seq = new ArrayList<Integer>(State.slices.keySet());
        Collections.shuffle(seq, new Random((long) (seed*100000)));

        ChangeLog log = null;
        for(int id : seq) {
            log = modifier.modify(State.slices.get(id));
            if(log.was_possible) {
                return log;
            }
        }

        if(log == null) {
            log = new ChangeLog(true);
            log.was_possible = false;
        }

        return log;
    }
}

interface SliceModifier {
    ChangeLog modify(Slice slice);
}

class StateSliceCreator extends StateModifier {

    public StateSliceCreator(double probability) {
        super(probability);
    }

    @Override
    public ChangeLog modify(double seed) {

        ChangeLog log = null;

        boolean finish = false;
        int i = (int) seed * PizzaLayout.R;
        while(true) {
            for(int j = 0; j < PizzaLayout.C; ++j) {
                log = Slice.createSlice(State.lastId, i, j);
                if(log.was_possible) {
                    return log;
                }
            }
            ++i;
            if(i == PizzaLayout.R) {
                if(finish) {
                    return log;
                } else {
                    finish = true;
                    i = 0;
                }
            }
        }
    }
}
