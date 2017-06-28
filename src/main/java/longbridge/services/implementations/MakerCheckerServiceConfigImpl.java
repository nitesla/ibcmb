package longbridge.services.implementations;

import longbridge.dtos.MakerCheckerDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.MakerChecker;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.services.MakerCheckerServiceConfig;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Locale;

/**
 * Created by chiomarose on 16/06/2017.
 */
@Service
public class MakerCheckerServiceConfigImpl implements MakerCheckerServiceConfig {
    @Autowired
    MakerCheckerRepo makerCheckerRepo;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();


    private ModelMapper modelMapper = new ModelMapper();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Verifiable(operation="Save_MakerChecker",description="Save Maker Checker")
    public String saveMakerChecker(MakerCheckerDTO makerCheckerDTO) throws InternetBankingException
    {

        try {
            MakerChecker checker = convertDTOToEntity(makerCheckerDTO);
            makerCheckerRepo.save(checker);
            logger.info("Added MakerChecker {}", checker.toString());
            return messageSource.getMessage("makerchecker.add.success", null, locale);
        }
        catch (Exception e)
        {
            throw new InternetBankingException(messageSource.getMessage("makerchecker.add.failure", null, locale), e);
        }

    }

    @Verifiable(operation="Update_MakerChecker",description="Update Maker Checker")
    public String updateMakerChecker(MakerChecker makerChecker) throws InternetBankingException
    {

        try {
            //  MakerChecker checker = convertDTOToEntity(makerChecker);
               makerCheckerRepo.save(makerChecker);
               logger.info("Added MakerChecker {}", makerChecker.toString());
              return messageSource.getMessage("makerchecker.add.success", null, locale);
           }
        catch (Exception e)
        {
            throw new InternetBankingException(messageSource.getMessage("makerchecker.add.failure", null, locale), e);
        }

    }

    @Override
    public boolean isMakerCheckerExist()
    {
        return true;
    }

    @Override
    public MakerChecker getEntity(String entityName)
    {
        return makerCheckerRepo.findFirstByOperation(entityName);
    }

    @Override
    public Iterable<MakerChecker> getAllEntities()
    {
        return makerCheckerRepo.findAll();
    }

    @Override
    public Page<MakerChecker> getEntities(Pageable pageDetails)
    {
        return makerCheckerRepo.findAll(pageDetails);
    }

    private MakerChecker convertEntityToDTO(MakerCheckerDTO makerCheckerDTO)
    {
        return modelMapper.map(makerCheckerDTO, MakerChecker.class);
    }

    private MakerChecker convertDTOToEntity(MakerCheckerDTO makerCheckerDTO)
    {
        return modelMapper.map(makerCheckerDTO,MakerChecker.class);
    }
}
