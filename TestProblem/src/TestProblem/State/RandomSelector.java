package TestProblem.State;

import java.util.List;

public class RandomSelector<T extends RandomSelector.ProbabilityItem> {

    private List<T> list;
    private double total_prob;

    public RandomSelector(List<T> list) {
        this.list = list;
        total_prob = 0;
        for(ProbabilityItem item : list) {
            double prob = item.getProbability();
            if(prob >= 0) {
                total_prob += item.getProbability();
            } else {
                list.remove(item);
            }
        }
    }

    public T getRandom() {
        if(list.size() == 0) {
            return null;
        }

        double rand = Math.random();
        double accumulated = 0;
        for(int i = 0; i < list.size(); ++i) {
            T item = list.get(i);
            if(accumulated + item.getProbability()/total_prob >= rand) {
                return item;
            } else {
                accumulated += item.getProbability()/total_prob;
            }
        }
        throw new RuntimeException();
    }

    public void remove(StateModifier modifier) {
        total_prob -= modifier.getProbability();
        list.remove(modifier);
    }


    public interface ProbabilityItem {

        double getProbability();
    }
}
