package longbridge.services.implementations;

import longbridge.models.Code;

import longbridge.repositories.CodeRepo;

import longbridge.services.CodeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Showboy on 29/03/2017.
 */
public class CodeServiceImpl implements CodeService {
    private CodeRepo codeRepo;

    @Autowired
    public CodeServiceImpl(CodeRepo codeRepository) {
        codeRepo = codeRepository;
    }


    @Override
    public void deleteCode(Long codeId) {

    }

    @Override
    public Code getCode(Long id) {
        return this.codeRepo.findOne(id);
    }

    @Override
    public Iterable<Code> getCodesofType(String codeType) {
        return this.codeRepo.findByType(codeType);
    }

    @Override
    public Iterable<Code> getCodes() {
        return this.codeRepo.findAll();
    }

    @Override
    public void addCode(Code code) {

    }


}