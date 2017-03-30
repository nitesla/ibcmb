package longbridge.services;

import longbridge.models.Code;

/**
 *This {@code CodeService} interface provides the methods for managing system codes
 */
public interface CodeService {

    void  addCode(Code code);

    void deleteCode(Long codeId);

    Code getCode(Long codeId);

    Iterable<Code> getCodes();
}
