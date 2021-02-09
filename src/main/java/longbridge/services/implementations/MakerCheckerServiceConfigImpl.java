package longbridge.services.implementations;

import longbridge.dtos.MakerCheckerDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.MakerChecker;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.services.MakerCheckerServiceConfig;
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

    private final Locale locale = LocaleContextHolder.getLocale();



    private final ModelMapper modelMapper = new ModelMapper();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public String configureMakerChecker(MakerChecker makerChecker)
    {

        try {
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

	@Override
	public Page<MakerChecker> findEntities(String pattern, Pageable pageDetails) {
		 return makerCheckerRepo.findUsingPattern(pattern,pageDetails);
	}
}
