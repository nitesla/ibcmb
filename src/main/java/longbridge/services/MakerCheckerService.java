package longbridge.services;

import longbridge.dtos.MakerCheckerDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.MakerChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by chiomarose on 16/06/2017.
 */

public interface MakerCheckerService {

    boolean isMakerCheckerExist(String operation);

    boolean isEnabled(String operation);

    MakerChecker getMakerChecker(String entityName);

    Iterable<MakerChecker> getAllEntities();

    Page<MakerChecker> getEntities(Pageable pageDetails);

    String addMakerChecker(MakerCheckerDTO chk) throws InternetBankingException;

    String updateMakerChecker(MakerChecker makerChecker) throws InternetBankingException;




}
