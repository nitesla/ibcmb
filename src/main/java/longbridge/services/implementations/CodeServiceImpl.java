package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import longbridge.repositories.CodeRepo;
import longbridge.services.CodeService;
import longbridge.services.VerificationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class CodeServiceImpl implements CodeService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private CodeRepo codeRepo;

    private VerificationService verificationService;

    private ModelMapper modelMapper;

    @Autowired
    public CodeServiceImpl(CodeRepo codeRepository, VerificationService verificationService, ModelMapper modelMapper) {
        codeRepo = codeRepository;
        this.verificationService = verificationService;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean deleteCode(Long codeId) {
        boolean result= false;

        try {
            this.codeRepo.delete(codeId);
            logger.info("Code {} HAS BEEN DELETED ",codeId.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());

        }
        return result;
    }

    @Override
    public CodeDTO getCode(Long id) {
        Code code = this.codeRepo.findOne(id);
        return convertEntityToDTO(code);
    }

    @Override
    public Code getByTypeAndCode(String type, String code) {
        return codeRepo.findByTypeAndCode(type,code);
    }

    @Override
    public Iterable<CodeDTO> getCodesByType(String codeType) {
        Iterable<Code> codes = this.codeRepo.findByType(codeType);
        return convertEntitiesToDTOs(codes);
    }

    @Override
    public Iterable<CodeDTO> getCodes() {
        Iterable<Code> codes = this.codeRepo.findAll();
        return convertEntitiesToDTOs(codes);    }

  

    @Transactional
    public String updateCode(CodeDTO codeDTO, AdminUser adminUser) {
        boolean result = false;
        Code code = convertDTOToEntity(codeDTO);
        //check if maker checker is enabled
        
        codeRepo.save(code);
        
        return  "" + result;
       // Code originalObject = codeRepo.findOne(code.getId());
        
        
    }

  
    public CodeDTO convertEntityToDTO(Code code){
        return  modelMapper.map(code,CodeDTO.class);
    }


    public Code convertDTOToEntity(CodeDTO codeDTO){
        return  modelMapper.map(codeDTO,Code.class);
    }

    public List<CodeDTO> convertEntitiesToDTOs(Iterable<Code> codes){
        List<CodeDTO> codeDTOList = new ArrayList<>();
        for(Code code: codes){
            CodeDTO codeDTO = convertEntityToDTO(code);
            codeDTOList.add(codeDTO);
        }
        return codeDTOList;
    }

    @Override
    public Code getByTypeAndCode(String type, String code) {
        return codeRepo.findByTypeAndCode(type,code);
    }

	@Override
	public Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public Page<CodeDTO> getCodes(Pageable pageDetails) {
        Page<Code> page = codeRepo.findAll(pageDetails);
        List<CodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<CodeDTO> pageImpl = new PageImpl<CodeDTO>(dtOs,pageDetails,t);
        return pageImpl;
    }


	@Override
	public String addCode(CodeDTO codeDTO) {
		Code code = convertDTOToEntity(codeDTO);
        try {
			return verificationService.addNewVerificationRequest(code);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.error("Error", e);
			return "Error adding Code " + e.getMessage();
		}
	}

}