package longbridge.services;

import longbridge.dtos.UnitDTO;
import longbridge.dtos.UnitPersonnelDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Created by Fortune on 4/26/2017.
 */
public interface UnitService {


    void addUnit(UnitDTO unitDTO);

    void updateUnit(UnitDTO unitDTO);

    void deleteUnit(Long id);

    UnitDTO getUnit(Long id);

    Iterable<UnitDTO> getUnits();

    Page<UnitDTO> getUnits(Pageable pageable);

    Iterable<UnitPersonnelDTO> getPersonnelFromUnit( String unitName);
}
