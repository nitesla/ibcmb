package longbridge.services.implementations;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.GreetingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.GreetingRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.DateUtil;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GreetingServiceImpl implements GreetingService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;

    private final GreetingRepo greetingRepo;

    private final ModelMapper modelMapper;
    @Autowired
    private EntityManager entityManager;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private AccountService accountService;

    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    public GreetingServiceImpl(GreetingRepo greetingRepo, ModelMapper modelMapper) {
        this.greetingRepo = greetingRepo;
        this.modelMapper = modelMapper;
    }
    /**
     this method is used to add greetings
     */

    @Override
    @Verifiable(operation = "ADD_GREETING", description = "Add Greeting")
    public String addGreeting(GreetingDTO greetingDTO) {


            try {
                if ((null != greetingRepo.findFirstByEventName(greetingDTO.getEventName()))) {
                    return messageSource.getMessage("greeting.add.exist", null, locale);
                }
                else{
                Greeting greeting = modelMapper.map(greetingDTO, Greeting.class);
                greeting.setCreatedBy(getCurrentUser().getFirstName()+" "+getCurrentUser().getLastName());
                greetingRepo.save(greeting);
                return messageSource.getMessage("greeting.add.success", null, locale);
                }
            }catch (VerificationInterruptedException e) {
                return e.getMessage();
            }catch (Exception e) {
                logger.info("greeting add failure ");
                throw new InternetBankingException(messageSource.getMessage("greeting.add.failure",null,locale),e);
            }


    }
    /**
    this method is used to retrieve greetings for the greeting view.html
     */
    @Override
    public Page<GreetingDTO> getGreetingDTOs(Pageable pageDetails) {
        Page<Greeting> greetings = greetingRepo.findAll(pageDetails);
        List<GreetingDTO> greetingDTOList = new ArrayList<>();
        for (Greeting greeting : greetings) {
            GreetingDTO greetingDTO = modelMapper.map(greeting, GreetingDTO.class);
            greetingDTOList.add(greetingDTO);
        }
        long t = greetings.getTotalElements();
        return new PageImpl<>(greetingDTOList, pageDetails, t);

    }
    /**
    used when retrieving a single greeting for Edit
    */
    @Override
    public GreetingDTO getGreetingDTO(Long greetingId) {
        Greeting greeting = greetingRepo.findOneById(greetingId);
        return modelMapper.map(greeting, GreetingDTO.class);
    }

    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_GREETING", description = "Update Greeting")
    public String updateGreeting(GreetingDTO dto) {
        try {
            Greeting greetingOld = greetingRepo.findOneById(dto.getId());
            entityManager.detach(greetingOld);
            Greeting greetingNew = modelMapper.map(dto, Greeting.class);
            greetingRepo.save(greetingNew);
            return messageSource.getMessage("greeting.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("greeting.update.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_GREETING", description = "Delete Greeting")
    public String deleteGreeting(Long id) {
        try {
            Greeting greeting = greetingRepo.findById(id).get();
            greetingRepo.delete(greeting);
            logger.info(String.valueOf(greeting));
            return messageSource.getMessage("greeting.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("greeting.delete.failure", null, locale), e);
        }
    }

    @Override
    public List<GreetingDTO> getCurrentGreetingsForUser()  {

        List<GreetingDTO> allCurrentGreetings = Stream.of(getAccountGreetings(), getBirthdayGreetings(),
                getPersonalGreetings(),getGeneralGreetings())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        logger.info("todayGreetings {}", allCurrentGreetings);

        return allCurrentGreetings;
    }



    private List<GreetingDTO> getGeneralGreetings() {
        List<GreetingDTO> todayGreetings  = new ArrayList<>();
        try {
            List<Greeting> greetings = greetingRepo.findGeneralGreetings(DateUtil.getToday());
            for (Greeting greeting : greetings) {
                if (DateUtil.isWithinCurrentDateRange(greeting.getExecutedOn(), greeting.getExpiredOn())) {
                    GreetingDTO greetingDTO = modelMapper.map(greeting, GreetingDTO.class);
                    greetingDTO.setMessage(setmessageParameters(greetingDTO, getCurrentUser()));
                    todayGreetings.add(greetingDTO);
                }
            }
        } catch (Exception e) {
            logger.info("General Greetings {}", e.getMessage());
        }
        return todayGreetings;

    }

    private  List<GreetingDTO> getPersonalGreetings() {
        List<GreetingDTO> personalGreetings = new ArrayList<>();
        try {
            List<Greeting> greetings = greetingRepo.findPersonalGreetings(DateUtil.getToday(),getCurrentUserId());
            logger.info("pgreeting {},{}",greetings,getCurrentUserId());
            for (Greeting greeting : greetings) {
                GreetingDTO greetingDTO = modelMapper.map(greeting, GreetingDTO.class);
                greetingDTO.setMessage(setmessageParameters(greetingDTO, getCurrentUser()));
                personalGreetings.add(greetingDTO);
            }

        } catch (Exception e) {
            logger.info("Personal Greetings {}", e.getMessage());
        }

        return personalGreetings;
    }

    private List<GreetingDTO> getBirthdayGreetings()  {
       List<GreetingDTO> greeting = new ArrayList<>();
        if ( UserType.RETAIL.equals(getCurrentUser().getUserType())) {
           RetailUser retailUser = modelMapper.map(getCurrentUser(), RetailUser.class);

                if(retailUser.getBirthDate() == null){
                    logger.info("Customers birthdate is not available{}","");
                }
                else if (DateUtil.isSameDayOfTheYear(retailUser.getBirthDate())) {
                    try {
                        Greeting message = greetingRepo.findFirstByEventName("BIRTHDAY");
                        GreetingDTO birthdayGreeting = modelMapper.map(message, GreetingDTO.class);
                        birthdayGreeting.setMessage(setmessageParameters(birthdayGreeting, getCurrentUser()));
                        greeting.add(birthdayGreeting);
                    }catch(Exception e){
                        logger.info("Birthday greeting has not been set {}", e.getMessage());
                    }
                }

        }
        return greeting;
    }

    private List<GreetingDTO> getAccountGreetings( ) {
        List<GreetingDTO> greetings =  new ArrayList<>();
        try {
            for (AccountDTO accountDTO : getAccountsForCurrentUser()) {
                String accountNumber = accountDTO.getAccountNumber();
                String acctOpenDate1 = integrationService.viewAccountDetails(accountNumber).getAcctOpenDate();

                Date acctOpenDate2 = format.parse(acctOpenDate1);
                //format.parse("2018-10-02")
                if (DateUtil.isSameDayOfTheYear(acctOpenDate2)) {
                    Greeting message = greetingRepo.findFirstByEventName("ACCOUNT_ANNIVERSARY");
                    GreetingDTO anniversaryDTO = modelMapper.map(message, GreetingDTO.class);
                    anniversaryDTO.setAccountNumber(accountDTO.getAccountNumber());
                    anniversaryDTO.setAccountName(accountDTO.getAccountName());
                    anniversaryDTO.setMessage(setmessageParameters(anniversaryDTO,getCurrentUser()));
                    greetings.add(anniversaryDTO);
                }
            }
        } catch (Exception e) {
            logger.info("Account Anniversary greeting  {}", e.getMessage());

        }
        return greetings;
    }


    public String setmessageParameters(GreetingDTO greetingDTO, User user) {
        String message = greetingDTO.getMessage();

        if (message.contains("${Firstname}")){
           message = message.replaceAll("\\$\\{Firstname\\}", user.getFirstName());
        }
        if (message.contains("${Lastname}")) message=message.replaceAll("\\$\\{Lastname\\}", user.getLastName());
        if (message.contains("${AccountNumber}")){
            if(greetingDTO.getAccountNumber()==null) {
                message = message.replaceAll("\\$\\{AccountNumber\\}", "");
            }else message=message.replaceAll("\\$\\{AccountNumber\\}", greetingDTO.getAccountNumber());
        }
        if (message.contains("${AccountName}")){
            if(greetingDTO.getAccountName()==null) {
                message = message.replaceAll("\\$\\{AccountName\\}", "");
            }else message=message.replaceAll("\\$\\{AccountName\\}", greetingDTO.getAccountName());
        }
        if (message.contains("${BirthDate}")) {
            RetailUser retailUser = modelMapper.map(user, RetailUser.class);
            message=message.replaceAll("\\$\\{BirthDate\\}", retailUser.getBirthDate().toString());
            logger.info("birthdate {}",retailUser.getBirthDate());
        }
        if (message.contains("${CurrentDate}")) {
            LocalDate current=LocalDate.now();
            message=message.replaceAll("\\$\\{CurrentDate\\}", current.toString());
        }
        return message;
    }


    private List<AccountDTO> getAccountsForCurrentUser(){
        User user=getCurrentUser();
        List<AccountDTO> accountList=new ArrayList<>();
        if(UserType.RETAIL.equals(user.getUserType())){
           RetailUser retailUser = retailUserService.getUserByName(user.getUserName());
            accountList=accountService.getAccounts(retailUser.getCustomerId());
        }
        if(UserType.CORPORATE.equals(user.getUserType())){
            CorporateUser corporateUser = corporateUserService.getUserByName(user.getUserName());
            accountList = accountService.getAccounts(corporateUser.getCorporate().getCustomerId());
        }
        return accountList;
    }

    private long getCurrentUserId(){
        User user=getCurrentUser();
        long userId=-1;
        if(UserType.RETAIL.equals(user.getUserType())){
            userId=retailUserService.getUserByName(user.getUserName()).getId();
        }
        if(UserType.CORPORATE.equals(user.getUserType())){
            userId=corporateUserService.getUserByName(user.getUserName()).getCorporate().getId();
        }
        return userId;
    }


    private User getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("current user {}",principal.getUser());
        return principal.getUser();
    }


}
