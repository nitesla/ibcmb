package longbridge.repositories;

import longbridge.models.AccountLimit;
import longbridge.models.CorpTransReqEntry;
import longbridge.models.CorporateRole;
import longbridge.models.CorporateUser;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface CorpTransReqEntryRepo extends CommonRepo<CorpTransReqEntry, Long>{


    boolean existsByTranReqIdAndRole(Long reqId, CorporateRole role);
}
