package longbridge.services;

import longbridge.dtos.FinancialInstitutionDTO;

/**
 * Created by Showboy on 24/04/2017.
 */
public interface FinancialInstitutionService {

    Iterable<FinancialInstitutionDTO> getFinancialInstitutions();


}
