package longbridge.services;

import longbridge.dtos.FinancialInstitutionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Showboy on 24/04/2017.
 */
public interface FinancialInstitutionService {

    Iterable<FinancialInstitutionDTO> getFinancialInstitutions();

    Page<FinancialInstitutionDTO> getFinancialInstitutions(Pageable pageDetails);
}
