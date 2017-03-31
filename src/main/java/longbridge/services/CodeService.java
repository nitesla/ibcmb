package longbridge.services;

import longbridge.models.Code;

/**
 *This {@code CodeService} interface provides the methods for managing system codes
 */
public interface CodeService {

    boolean  addCode(Code code);

    boolean deleteCode(Long codeId);

    Code getCode(Long codeId);

    Iterable<Code> getCodesofType(String codeType);

    Iterable<Code> getCodes();

}
