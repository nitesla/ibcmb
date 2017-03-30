package longbridge.services.implementations;

import longbridge.models.Code;
import longbridge.repositories.CodeRepo;
import longbridge.services.CodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Showboy on 29/03/2017.
 */
public class CodeServiceImpl implements CodeService{

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private CodeRepo codeRepo;

    @Autowired
    public CodeServiceImpl(CodeRepo codeRepository){
        codeRepo = codeRepository;
    }

    @Override
    public boolean addCode(Code code) {
        boolean result= false;

        try {
            this.codeRepo.save(code);
            logger.info("Code {} HAS BEEN ADDED ",code.getId());
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
        return result;
    }

    @Override
    public boolean deleteCode(Long codeId) {
        boolean result= false;

        try {
            Code delCode = this.codeRepo.findOne(codeId);
            delCode.setDelFlag("Y");
            this.codeRepo.save(delCode);
            logger.info("Code {} HAS BEEN DELETED ");
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

        return this.codeRepo.findByTypeandDelFlag(codeType, "N");
    }

    @Override
    public Iterable<Code> getCodes() {
        return this.codeRepo.findByDelFlag("N");
    }

}
