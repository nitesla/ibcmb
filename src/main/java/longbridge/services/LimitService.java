package longbridge.services;

import longbridge.dtos.GlobalLimitDTO;
import longbridge.models.GlobalLimit;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface LimitService  {

    void addGlobalLimit(GlobalLimitDTO globalLimit);

     GlobalLimitDTO getGlobalCorporateLimit(Long id);

    GlobalLimitDTO getGlobalRetailLimit(Long id);


    List<GlobalLimitDTO> getCorporateGlobalLimits();

    List<GlobalLimitDTO> getRetailGlobalLimits();
}
