package comparators;

import phrases.Phrase;

import java.util.Comparator;

public class AlphabeticComparator implements Comparator<Phrase> {
    @Override
    public int compare(Phrase o1, Phrase o2) {
        if (o1.getEngWord().compareTo(o2.getEngWord()) == 0) {
            return 1;
        }
        return o1.getEngWord().compareTo(o2.getEngWord());
    }
}
