package longbridge.repositories;

import longbridge.models.InvestmentRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRateRepo extends CommonRepo<InvestmentRate,Long> {

    @Query("select distinct r.investmentName from InvestmentRate  r")
    List<String> findDistinctFirstByInvestmentName();

    Page<InvestmentRate> findAllByInvestmentName(String investmentName, Pageable pageable);

    @Query("select f.value from InvestmentRate f where f.investmentName=:investmentName and f.tenor=:tenor and :amount between f.minAmount and f.maxAmount")
    Optional<Integer> findRateByTenorAndAmount(@Param("investmentName") String investmentName, @Param("tenor") int tenor, @Param("amount") int amount);

    List<InvestmentRate> findByInvestmentName(String investmentName);
}
