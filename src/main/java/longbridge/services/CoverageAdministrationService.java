package longbridge.services;


import longbridge.dtos.AddCoverageDTO;
import longbridge.dtos.CoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CoverageAdministrationService {

   Page<CoverageDTO> getAllCoverage(EntityId id, Pageable pageDetails);
   void updateCoverage(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException;
   void addCoverage(AddCoverageDTO addCoverageDTO);
   Coverage getCoverage(EntityId id, String code);
   void addCoverageForNewEntity();
   void addCoverageForNewCorporate(Corporate corporate);
   void addCoverageForNewRetail(RetailUser retailUser);
   void addCoverageForNewCodes(Code code);
   void newCodes(String code);

}
