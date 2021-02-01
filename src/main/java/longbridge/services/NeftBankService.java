package longbridge.services;

import longbridge.dtos.NeftBankDTO;
import longbridge.dtos.NeftBankNameDTO;
import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NeftBankService {

    String addNeftBank(NeftBankDTO code) throws InternetBankingException;
    String deletNeftBank(Long codeId) throws InternetBankingException;
    NeftBankDTO getNeftBank(Long codeId);
    List<NeftBankDTO> getNeftBranchesByBankName(String bankName);
    String updateNeftBank(NeftBankDTO neftBankDTO) throws InternetBankingException;
    Page<NeftBankDTO> getNeftBranchesByBankName(String bankName, Pageable pageDetails);
    Page<NeftBankNameDTO> getNeftBankNames(Pageable pageDetails);
    Page<NeftBankDTO> getNeftBanks(Pageable pageDetails);
    List<NeftBankDTO> getNeftBankList();

    Page<NeftBankNameDTO> getNeftBankNames(String search, Pageable pageable);
}
