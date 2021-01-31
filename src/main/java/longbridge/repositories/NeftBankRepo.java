package longbridge.repositories;

import longbridge.dtos.NeftBankNameDTO;
import longbridge.models.NeftBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeftBankRepo extends CommonRepo<NeftBank, Long> {


    @Query("select distinct bank.bankName from NeftBank bank")
    Page<String> findAllBanks(Pageable pageDetails);
    List<NeftBank> findAllByBankName(String bankName);
    Page<NeftBank> findAllByBankName(String bankName, Pageable pageable);
    NeftBank findByBankNameAndBranchNameAndSortCode(String bankName, String branchName, String sortCode);

    @Query("select distinct bank.bankName from NeftBank bank where upper(bank.bankName) like upper(concat('%', :pattern,'%')) ")
    Page<String> searchByBank(@Param("pattern") String pattern, Pageable pageable);
}
