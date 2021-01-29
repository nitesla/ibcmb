package longbridge.repositories;

import longbridge.models.NeftBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NeftBankRepo extends CommonRepo<NeftBank, Long> {
    List<NeftBank> findAllByBankName(String bankName);
    Page<NeftBank> findAllByBankName(String bankName, Pageable pageable);
    NeftBank findByBankNameAndBranchNameAndSortCode(String bankName, String branchName, String sortCode);
}
