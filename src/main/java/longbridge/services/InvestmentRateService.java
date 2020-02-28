package longbridge.services;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.InvestmentRateDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.InvestmentRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvestmentRateService {

    List<String> getDistinctInvestments();

    @PreAuthorize("hasAuthority('ADD_RATE')")
    String addRate(InvestmentRateDTO investmentRateDTO) throws InternetBankingException;

    Page<InvestmentRate> getAllRatesByInvestmentName(String investmentName, Pageable pageable);

    InvestmentRateDTO getInvestmentRate(Long rateId);

    @PreAuthorize("hasAuthority('UPDATE_RATE')")
    String updateRate(InvestmentRateDTO investmentRateDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_RATE')")
    String deleteRate(Long rateId) throws InternetBankingException;

    Optional<Integer> getRateByTenorAndAmount(String investmentName, int tenor, int amount);

    List<InvestmentRate> getInvestmentRateByInvestmentName(String investmentName);


}
