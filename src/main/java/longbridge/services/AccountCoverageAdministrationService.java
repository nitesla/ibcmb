package longbridge.services;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.AddCoverageDTO;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;
import longbridge.models.EntityId;
import longbridge.models.UserType;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface AccountCoverageAdministrationService {

   Page<AccountCoverageDTO> getAllCoverage(EntityId id, Pageable pageDetails);
   void updateCoverage(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException;
   void addCoverage(AddCoverageDTO addCoverageDTO);
   AccountCoverage getCoverage(EntityId id, String code);

}
