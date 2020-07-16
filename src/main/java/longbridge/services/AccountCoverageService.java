package longbridge.services;


import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;

import java.util.List;


public interface AccountCoverageService {

   String addCoverage(Long corpId,Long codeId)throws InternetBankingException;
   List<CodeDTO> getCoverage();

//   String enableCoverage(String coverageJson) throws InternetBankingException, IOException;
//   String deleteCoverage(Long coverageId) throws InternetBankingException;
//   List<String> enabledCoverageList();
//   Iterable<AccountCoverageDTO> getAllCoverage();
//   Long getCoverageId(String coverageCode);
//   Long getCodeId(String code);



    }
