package longbridge.repositories;

import longbridge.models.CorpTransferRule;
import longbridge.models.Corporate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */
@Repository
public interface CorpTransferRuleRepo extends CommonRepo<CorpTransferRule,Long> {

    List<CorpTransferRule> findByCorporate(Corporate corporate);
}
