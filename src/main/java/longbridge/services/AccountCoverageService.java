package longbridge.services;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Code;


public interface AccountCoverageService {

   String addCoverage(Code code)throws InternetBankingException;
   String updateCoverage(String id);
   Iterable<AccountCoverageDTO> getAllCoverage();


    }
