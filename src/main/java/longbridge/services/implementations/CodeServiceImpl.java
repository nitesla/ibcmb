package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Code;

import longbridge.models.OperationCode;
import longbridge.models.Verification;
import longbridge.repositories.CodeRepo;

import longbridge.repositories.VerificationRepo;
import longbridge.services.CodeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class CodeServiceImpl implements CodeService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private CodeRepo codeRepo;

    private VerificationRepo verificationRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    public CodeServiceImpl(CodeRepo codeRepository, VerificationRepo verificationRepo) {
        codeRepo = codeRepository;
        this.verificationRepo = verificationRepo;
    }


    @Override
    public boolean deleteCode(Long codeId) {
        boolean result= false;

        try {

            Code code = codeRepo.findOne(codeId);
            code.setDelFlag("Y");
            this.codeRepo.save(code);
            logger.info("Code {} HAS BEEN DELETED ",code.toString());
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
    public Iterable<CodeDTO> getCodesByType(String codeType) {
        Iterable<Code> codes = this.codeRepo.findByType(codeType);
        return convertEntitiesToDTOs(codes);
    }

    @Override
    public Iterable<CodeDTO> getCodes() {
        Iterable<Code> codes = this.codeRepo.findAll();
        return convertEntitiesToDTOs(codes);    }

    /**
     * Adds a new code to the syste
     * @param codeDTO the code
     */
    @Override
   public void add(CodeDTO codeDTO, AdminUser adminUser) {

//        Code code = convertDTOToEntity(codeDTO);
        try {
            Verification verification = new Verification();
            verification.setBeforeObject("");
            verification.setAfterObject(serialize(codeDTO));
            verification.setOriginal("");
            verification.setDescription("Added a new Code");
            verification.setOperationCode(OperationCode.ADD_CODE);
            verification.setInitiatedBy(adminUser);
            verification.setInitiatedOn(new Date());
            verificationRepo.save(verification);

            logger.info("Code creation request has been added ");
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
    }
//
//    public boolean updateCode(CodeDTO codeDTO, AdminUser adminUser){
//         boolean result= false;
//         Code code = convertDTOToEntity(codeDTO);
//         Code originalObject = codeRepo.findOne(code.getId());
    /**
     * Modifies an existing code
     * @param code the code
     */
    @Override
    public void modify(CodeDTO code, AdminUser adminUser){
        Code codeO = codeRepo.findOne(code.getId());
        CodeDTO originalObject = convertEntityToDTO(codeO);

        try {
            Verification verification = new Verification();
            verification.setBeforeObject(serialize(originalObject));
            verification.setAfterObject(serialize(code));
            verification.setOriginal(serialize(originalObject));
            verification.setDescription("Modified a Code");
            verification.setOperationCode(OperationCode.MODIFY_CODE);
            verification.setInitiatedBy(adminUser);
            verification.setInitiatedOn(new Date());
            verificationRepo.save(verification);

            logger.info("Code modification request has been added ");
        }
        catch (Exception e){
            logger.error("Error Occurred {}",e);
        }
    }

    @Override
    public CodeDTO deserialize(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CodeDTO code = mapper.readValue(data, CodeDTO.class);
        return code;
    }

    @Override
    public String serialize(CodeDTO code) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(code);
        return data;
    }

    @Override
    public void verify(Verification verificationObject, AdminUser verifier) throws IOException {
        verificationObject.setVerifiedBy(verifier);
        verificationObject.setVerifiedOn(new Date());

        CodeDTO afterCodeDTO = deserialize(verificationObject.getAfterObject());
        Code afterCode = convertDTOToEntity(afterCodeDTO);

        codeRepo.save(afterCode);
        verificationObject.setEntityId(afterCode.getId());
        verificationRepo.save(verificationObject);
    }

    @Override
    public void decline(Verification verificationObject, AdminUser decliner, String declineReason) {
        verificationObject.setDeclinedBy(decliner);
        verificationObject.setDeclinedOn(new Date());
        verificationObject.setDeclineReason(declineReason);
        //save verification
        verificationRepo.save(verificationObject);
    }

    private CodeDTO convertEntityToDTO(Code code){
        return  modelMapper.map(code,CodeDTO.class);
    }

    private Code convertDTOToEntity(CodeDTO codeDTO){
        return  modelMapper.map(codeDTO,Code.class);
    }

    private Iterable<CodeDTO> convertEntitiesToDTOs(Iterable<Code> codes){
        List<CodeDTO> codeDTOList = new ArrayList<>();
        for(Code code: codes){
            CodeDTO codeDTO = convertEntityToDTO(code);
            codeDTOList.add(codeDTO);
        }
        return codeDTOList;
    }


	@Override
	public Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}
}