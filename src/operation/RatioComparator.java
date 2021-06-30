package operation;

import phrases.Phrase;

public class RatioComparator implements java.util.Comparator<Phrase> {

    @Override
    public int compare(Phrase o1, Phrase o2) {
        int result = Double.compare(o1.getRatio(), o2.getRatio());
        if(result == 0){
            return o1.compareTo(o2);
        }
        return result;
    }
}
