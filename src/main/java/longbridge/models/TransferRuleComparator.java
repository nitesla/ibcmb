package longbridge.models;

import java.util.Comparator;

/**
 * Created by Fortune on 5/20/2017.
 */
public class TransferRuleComparator implements Comparator<CorpTransferRule> {
    @Override
    public int compare(CorpTransferRule rule1, CorpTransferRule rule2) {
        return rule1.getUpperLimitAmount().compareTo(rule2.getUpperLimitAmount());
    }
}
