package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.models.AdminUser;
import longbridge.models.Code;

import longbridge.models.Verification;
import longbridge.repositories.CodeRepo;

import longbridge.repositories.VerificationRepo;
import longbridge.services.CodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class CodeServiceImpl implements CodeService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private CodeRepo codeRepo;

    private VerificationRepo verificationRepo;

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
    public Code getCode(Long id) {
        return this.codeRepo.findOne(id);
    }

    @Override
    public Iterable<Code> getCodesofType(String codeType) {
        return this.codeRepo.findByTypeAndDelFlag(codeType, "N");
    }

    @Override
    public Iterable<Code> getCodes() {
        return this.codeRepo.findByDelFlag("N");
    }

    @Override
    public boolean addCode(Code code, AdminUser adminUser) {
        boolean result= false;

        try {
            Verification verification = new Verification();
            verification.setBeforeObject("");
            verification.setAfterObject(serialize(code));
            verification.setOriginal("");
            verification.setDescription("");
            verification.setOperationCode("rogaroga");
            verification.setInitiatedBy(adminUser);
            verificationRepo.save(verification);

            logger.info("Code creation request has been added ");
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
        return result;
    }


    @Override
    public Code deserialize(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Code code = mapper.readValue(data, Code.class);
        return code;
    }

    @Override
    public String serialize(Code code) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(code);
        return data;
    }

    @Override
    public void verify(Verification verificationObject, AdminUser verifier) throws IOException {
        verificationObject.setVerifiedBy(verifier);
        verificationObject.setVerifiedOn(new Date());
        Code code = deserialize(verificationObject.getAfterObject());
        codeRepo.save(code);
        verificationObject.setVerifiedId(code.getId());
        //save verification
    }

    @Override
    public void decline(Verification verificationObject, AdminUser decliner, String declineReason) {
        verificationObject.setDeclinedBy(decliner);
        verificationObject.setDeclinedOn(new Date());
        verificationObject.setDeclineReason(declineReason);
        //save verification
    }
}