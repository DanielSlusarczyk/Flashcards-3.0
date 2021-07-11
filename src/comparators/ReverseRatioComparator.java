package comparators;

import phrases.Phrase;

import java.util.Comparator;

public class ReverseRatioComparator implements Comparator<Phrase> {

    @Override
    public int compare(Phrase o1, Phrase o2) {
        int result = Double.compare(o2.getRatio(), o1.getRatio());
        if(result == 0){
            return o2.compareTo(o1);
        }
        return result;
    }
}
