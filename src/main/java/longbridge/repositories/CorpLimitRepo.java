package longbridge.repositories;

import longbridge.models.CorpLimit;
import longbridge.models.Corporate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Iterator;
import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/6/2017.
 */
public interface CorpLimitRepo extends CommonRepo<CorpLimit, Long> {
    List<CorpLimit> findByCorporate(Corporate corporate);
}
