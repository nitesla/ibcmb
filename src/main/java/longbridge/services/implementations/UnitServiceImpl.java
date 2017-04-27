package longbridge.services.implementations;

import longbridge.dtos.UnitDTO;
import longbridge.dtos.UnitPersonnelDTO;
import longbridge.models.Unit;
import longbridge.models.PersonnelContact;
import longbridge.repositories.UnitPersonnelRepo;
import longbridge.repositories.UnitRepo;
import longbridge.services.UnitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fortune on 4/26/2017.
 */

@Service
public class UnitServiceImpl implements UnitService {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UnitPersonnelRepo unitPersonnelRepo;

    @Autowired
    UnitRepo unitRepo;




    @Override
    @Transactional
    public void addUnit(UnitDTO unitDTO) {
        Unit unit = convertUnitDTOToEntity(unitDTO);
        Iterator<PersonnelContact> personnelIterator = unit.getPersonnel().iterator();
        while (personnelIterator.hasNext()){
            PersonnelContact personnel = personnelIterator.next();
            if(personnel.getName()==null){
                personnelIterator.remove();
            }
            else {
                personnel.setUnit(unit);
            }
        }
        unitRepo.save(unit);
    }

    @Override
    @Transactional
    public void updateUnit(UnitDTO unitDTO){
        Unit unit = unitRepo.findOne(unitDTO.getId());
        List<PersonnelContact> personnelList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        for(UnitPersonnelDTO personnel: unitDTO.getPersonnel()){
            PersonnelContact personnelContact;
            if(personnel.getName()==null){
                continue;
            }
            if(personnel.getId()==null){
                personnelContact = new PersonnelContact();
                personnelContact.setUnit(unit);
            }
            else {
                personnelContact = unitPersonnelRepo.findOne(personnel.getId());
            }
            modelMapper.map(personnel, personnelContact);
            personnelList.add(personnelContact);
        }
        unit.setId(unitDTO.getId());
        unit.setVersion(unitDTO.getVersion());
        unit.setCode(unitDTO.getCode());
        unit.setName(unitDTO.getName());
        unit.setPersonnel(personnelList);
        unitRepo.save(unit);

    }

    @Override
    public void deleteUnit(Long id){
        unitRepo.delete(id);
    }

    @Override
    public UnitDTO getUnit(Long id) {
        return convertUnitEntityToDTO(unitRepo.findOne(id));
    }

    @Override
    public Iterable<UnitDTO> getUnits() {
        return convertUnitEntitiesToDTOs(unitRepo.findAll());
    }

   @Override
    public Page<UnitDTO> getUnits(Pageable pageDetails) {
        Page<Unit> page = unitRepo.findAll(pageDetails);
        List<UnitDTO> dtOs = convertUnitEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<UnitDTO> pageImpl = new PageImpl<UnitDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Iterable<UnitPersonnelDTO> getPersonnelFromUnit(String unitName) {
        Iterable<PersonnelContact> unitPersonnel = unitPersonnelRepo.findByUnit(unitName);
        return convertEntitiesToDTos(unitPersonnel);
    }

    private UnitPersonnelDTO convertEntityToDTO(PersonnelContact personnel){
        return modelMapper.map(personnel,UnitPersonnelDTO.class);
    }

    private PersonnelContact convertDTOToEntity(UnitPersonnelDTO personnelDTO){
        return modelMapper.map(personnelDTO,PersonnelContact.class);
    }

    private Unit convertUnitDTOToEntity(UnitDTO dto){
        return modelMapper.map(dto,Unit.class);
    }

    private UnitDTO convertUnitEntityToDTO(Unit unit){
        return modelMapper.map(unit,UnitDTO.class);
    }

    private Iterable<PersonnelContact> convertDTOsToEntities(Iterable<UnitPersonnelDTO> personnelDTOs){
        List<PersonnelContact> personnelList = new ArrayList<>();
        for(UnitPersonnelDTO personnelDTO: personnelDTOs){
            personnelList.add(convertDTOToEntity(personnelDTO));
        }
        return personnelList;
    }

    private Iterable<UnitPersonnelDTO> convertEntitiesToDTos(Iterable<PersonnelContact> personnels){
        List<UnitPersonnelDTO> personnelDTOList = new ArrayList<>();
        for(PersonnelContact personnel: personnels){
            personnelDTOList.add(convertEntityToDTO(personnel));
        }
        return personnelDTOList;
    }

    private List<UnitDTO> convertUnitEntitiesToDTOs(Iterable<Unit> units){
        List<UnitDTO> unitDTOList = new ArrayList<>();
        for(Unit unit: units){
            unitDTOList.add(convertUnitEntityToDTO(unit));
        }
        return unitDTOList;
    }
}
