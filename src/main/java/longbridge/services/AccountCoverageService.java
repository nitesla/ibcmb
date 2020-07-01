package longbridge.services;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface AccountCoverageService {



   Page<AccountCoverageDTO> getAllCoverageForCorporate(Long corpId, Pageable pageDetails);
   String enableCoverageForCorporate(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException;
   void addCoverageForCorporate(Long corpId,Long codeId);
   List<AccountCoverage> getEnabledCoverageForCorporate(Long corpId);
   Boolean enabledCoverageExist(Long corpId);
   String getCustomerNumber(Long corpId);
   Page<AccountCoverageDTO> getAllCoverageForRetail(Long retId,Pageable pageDetails);
   JSONObject getAllEnabledCoverageDetailsForCorporate(Long corpId);





    }
