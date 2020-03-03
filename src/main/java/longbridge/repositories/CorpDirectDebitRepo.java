package longbridge.repositories;

import longbridge.models.CorpDirectDebit;
import longbridge.models.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 26/02/2018.
 */
public interface CorpDirectDebitRepo extends CommonRepo<CorpDirectDebit , Long> {
    List<CorpDirectDebit> findByNextDebitDateEquals(Date date);

   // Page<DirectDebit> findByCorporate(Long corporate, Pageable pageable);

    List<CorpDirectDebit> findByNextDebitDateBetween(Date start, Date end);
}
