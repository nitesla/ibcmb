package longbridge.repositories;

import longbridge.models.CorpTransRule;
import longbridge.models.Corporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */
@Repository
public interface CorpTransferRuleRepo extends CommonRepo<CorpTransRule,Long> {

    List<CorpTransRule> findByCorporate(Corporate corporate);

    Page<CorpTransRule> findByCorporate(Corporate corporate, Pageable pageable);

}
