package longbridge.services.implementations;

import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.ConfigurationService;
import longbridge.services.CorpProfileUpdateService;
import longbridge.services.SecurityService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Created by Showboy on 08/07/2017.
 */
@Service
public class CorpProfileUpdateServiceImpl implements CorpProfileUpdateService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private CorporateUserRepo corporateUserRepo;

    private Locale locale = LocaleContextHolder.getLocale();


    private void addUserContact(String username, String group, String phone, String email){
        try{
            securityService.addUserContacts(email, phone, true, username, group);
        }catch (InternetBankingSecurityException e){
            securityService.deleteEntrustUser(username, group);
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void setEntrustUserQA(String username, String group,  List<String> securityQuestion, List<String> securityAnswer){
        try{
            securityService.setUserQA(username, group, securityQuestion, securityAnswer);
        }catch (InternetBankingSecurityException e){
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void setEntrustUserMutualAuth(String username, String group, String captionSec, String phishingSec){
        try{
            securityService.setMutualAuth(username, group, captionSec, phishingSec);
        }catch (InternetBankingSecurityException e){
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private CorporateUser convertDTOToEntity(CorporateUserDTO corporateUserDTO) {
        return modelMapper.map(corporateUserDTO, CorporateUser.class);
    }

    @Override
    public String completeEntrustProfileCreation(CorporateUserDTO user) {

        try {
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {

                    setEntrustUserQA(user.getEntrustId(), user.getEntrustGroup(), user.getSecurityQuestion(), user.getSecurityAnswer());

                    setEntrustUserMutualAuth(user.getEntrustId(), user.getEntrustGroup(), user.getCaptionSec(), user.getPhishingSec());

                    CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());

                    corporateUser.setIsFirstTimeLogon("N");

                    corporateUser.setUserType(UserType.CORPORATE);

                    corporateUserRepo.save(corporateUser);
                }
            }

            logger.info("Corporate user {} updated", user.getUserName());
            return messageSource.getMessage("profile.update.success", null, locale);
        }catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("profile.update.failure", null, locale), e);
        }
    }
}
