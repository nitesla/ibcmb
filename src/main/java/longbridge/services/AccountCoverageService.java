package longbridge.services;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface AccountCoverageService {



   Page<AccountCoverageDTO> getAllCoverageForCorporate(Long corpId, Pageable pageDetails);
   String enableCoverageForCorporate(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException;
   void addCoverageForCorporate(Long corpId,Long codeId);
   List<AccountCoverage> getEnabledCoverageForCorporate(Long corpId);
   List<CoverageDetailsDTO> getAllEnabledCoverageDetailsForCorporate(Long corpId);
   Page<AccountCoverageDTO> getAllCoverageForRetailUser(Long retId, Pageable pageDetails);
   String enableCoverageForRetailUser(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException;
   void addCoverageForRetailUser(Long retId,Long codeId);
   List<AccountCoverage> getEnabledCoverageForRetailUser(Long retId);
   List<CoverageDetailsDTO> getAllEnabledCoverageDetailsForRetailUser(Long retId);
   List<CoverageDetailsDTO> getAllEnabledCoverageDetailsForCustomer(String customerId);





}
