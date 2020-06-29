package longbridge.services;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;


public interface AccountCoverageService {



   Page<AccountCoverageDTO> getAllCoverageForCorporate(Long corpId, Pageable pageDetails);
   String enableCoverage(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException, IOException;
   void addCoverage(Long corpId,Long codeId);
   List<AccountCoverage> getEnabledCoverageForCorporate(Long corpId);
   Boolean enabledCoverageExist(Long corpId);
   String getCustomerNumber(Long corpId);





    }
