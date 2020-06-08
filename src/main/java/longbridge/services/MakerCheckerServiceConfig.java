package longbridge.services;

import longbridge.exception.InternetBankingException;
import longbridge.models.MakerChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by chiomarose on 16/06/2017.
 */

public interface MakerCheckerServiceConfig {

    boolean isMakerCheckerExist();

    MakerChecker getEntity(String entityName);

    Iterable<MakerChecker> getAllEntities();

    Page<MakerChecker> getEntities(Pageable pageDetails);

    Page<MakerChecker> findEntities(String pattern, Pageable pageDetails);

    @PreAuthorize("hasAuthority('MAKER_CHECKER_CONFIG')")
    String configureMakerChecker(MakerChecker makerChecker) throws InternetBankingException;




}
