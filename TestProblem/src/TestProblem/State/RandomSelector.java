package TestProblem.State;

import java.util.List;

public class RandomSelector<T extends RandomSelector.ProbabilityItem> {

    private List<T> list;
    private double total_prob;

    public RandomSelector(List<T> list) {
        this.list = list;
        total_prob = 0;
        for(ProbabilityItem item : list) {
            total_prob += item.getProbability();
        }
    }

    public T popRandom() {
        if(list.size() == 0) {
            return null;
        }

        double rand = Math.random();
        double accumulated = 0;
        for(int i = 0; i < list.size(); ++i) {
            T item = list.get(i);
            if(accumulated + item.getProbability()/total_prob >= rand) {
                total_prob -= item.getProbability();
                list.remove(i);
                return item;
            } else {
                accumulated += item.getProbability()/total_prob;
            }
        }
        System.out.println("ERROR");
        System.exit(1);
        return null;
    }


    public interface ProbabilityItem {

        double getProbability();
    }
}