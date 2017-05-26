package longbridge.models;

import java.util.Comparator;

/**
 * Created by Fortune on 5/20/2017.
 */
public class TransferRuleComparator implements Comparator<CorpTransRule> {
    @Override
    public int compare(CorpTransRule rule1, CorpTransRule rule2) {
        return rule1.getUpperLimitAmount().compareTo(rule2.getUpperLimitAmount());
    }
}
