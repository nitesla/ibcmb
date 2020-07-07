package longbridge.services;


import longbridge.config.CoverageInfo;
import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.AddCoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;
import longbridge.models.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface AccountCoverageService {

   List<CoverageInfo> getCoverageDetails(EntityId id);

}
