package longbridge.services;

import longbridge.dtos.BillerDTO;
import longbridge.models.Billers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillerService {
    public String updateBillersTable();
//    public List<Billers> getAllBillerList();
//    public Page<BillerDTO> getBillers(Pageable pageable);
//    List<BillerDTO> convertEntitiesToDTOs(Iterable<Billers> verifications);
//    public BillerDTO convertEntityToDTO(Billers verification);
}
