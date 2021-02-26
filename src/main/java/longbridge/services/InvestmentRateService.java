package longbridge.services;

import longbridge.dtos.InvestmentRateDTO;
import longbridge.models.InvestmentRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface InvestmentRateService {

    List<String> getDistinctInvestments();

    @PreAuthorize("hasAuthority('ADD_RATE')")
    String addRate(InvestmentRateDTO investmentRateDTO) ;

    Page<InvestmentRate> getAllRatesByInvestmentName(String investmentName, Pageable pageable);

    InvestmentRateDTO getInvestmentRate(Long rateId);

    @PreAuthorize("hasAuthority('UPDATE_RATE')")
    String updateRate(InvestmentRateDTO investmentRateDTO) ;

    @PreAuthorize("hasAuthority('DELETE_RATE')")
    String deleteRate(Long rateId) ;

    Optional<Integer> getRateByTenorAndAmount(String investmentName, int tenor, int amount);

    List<InvestmentRate> getInvestmentRateByInvestmentName(String investmentName);


}
