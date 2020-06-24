package longbridge.services;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;

import java.io.IOException;
import java.util.List;


public interface AccountCoverageService {

   String addCoverage(CodeDTO codeDTO)throws InternetBankingException;
   String enableCoverage(String coverageJson) throws InternetBankingException, IOException;
   String deleteCoverage(Long coverageId) throws InternetBankingException;
   List<String> enabledCoverageList();
   Iterable<AccountCoverageDTO> getAllCoverage();
   Long getCoverageId(String coverageCode);
   Long getCodeId(String code);



    }
