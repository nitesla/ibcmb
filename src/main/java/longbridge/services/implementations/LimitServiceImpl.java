package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.dtos.SettingDTO;
import longbridge.models.AdminUser;
import longbridge.models.GlobalLimit;
import longbridge.models.Setting;
import longbridge.models.UserType;
import longbridge.repositories.GlobalLimitRepo;
import longbridge.services.CodeService;
import longbridge.services.LimitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
@Service
public class LimitServiceImpl implements LimitService{

    @Autowired
    GlobalLimitRepo globalLimitRepo;

    @Autowired
    CodeService codeService;

    @Autowired
    ModelMapper modelMapper;

    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public void addGlobalLimit(GlobalLimitDTO globalLimitDTO) {
    GlobalLimit globalLimit = convertDTOToEntity(globalLimitDTO);
    globalLimitRepo.save(globalLimit);
    }

    @Override
    public GlobalLimitDTO getGlobalCorporateLimit(Long id) {
        GlobalLimit globalLimit = globalLimitRepo.findOne(id);
        return convertEntityToDTO(globalLimit);
    }

    @Override
    public GlobalLimitDTO getGlobalRetailLimit(Long id) {
        GlobalLimit globalLimit = globalLimitRepo.findOne(id);
        return convertEntityToDTO(globalLimit);
    }

    @Override
    public List<GlobalLimitDTO> getCorporateGlobalLimits() {
        List<GlobalLimit> limitsDTOs = globalLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        List<GlobalLimitDTO> dtOs = convertEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }

    @Override
    public List<GlobalLimitDTO> getRetailGlobalLimits() {
        List<GlobalLimit> limitsDTOs = globalLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        List<GlobalLimitDTO> dtOs = convertEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }


    private GlobalLimitDTO convertEntityToDTO(GlobalLimit limit){
        GlobalLimitDTO globalLimitDTO = modelMapper.map(limit, GlobalLimitDTO.class);
        globalLimitDTO.setStartDate(dateFormatter.format(limit.getEffectiveDate()));
        globalLimitDTO.setEffectiveDate(limit.getEffectiveDate());
        globalLimitDTO.setFrequency(codeService.getByTypeAndCode("FREQUENCY",limit.getFrequency()).getDescription());
        return globalLimitDTO;
    }

    private GlobalLimit convertDTOToEntity(GlobalLimitDTO limit){
        GlobalLimit globalLimit = modelMapper.map(limit, GlobalLimit.class);
        try {
            globalLimit.setEffectiveDate(dateFormatter.parse(limit.getStartDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return globalLimit;
    }


    private List<GlobalLimitDTO> convertEntitiesToDTOs(Iterable<GlobalLimit> globalLimits){
        List<GlobalLimitDTO> limitDTOList = new ArrayList<>();
        for(GlobalLimit globalLimit: globalLimits){
            GlobalLimitDTO limitDTO = convertEntityToDTO(globalLimit);
            limitDTOList.add(limitDTO);
        }
        return limitDTOList;
    }

}
