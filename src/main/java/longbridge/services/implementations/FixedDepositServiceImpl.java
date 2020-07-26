package longbridge.services.implementations;

import longbridge.dtos.ContactDTO;
import longbridge.dtos.FixedDepositDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.UserGroupRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.FIxedDepositActions;
import longbridge.utils.Response;
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
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by mac on 08/03/2018.
 */
@Service
public class FixedDepositServiceImpl implements FixedDepositService {
    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserGroupRepo userGroupRepo;
    @Autowired
    private UserGroupService groupService;
    @Autowired
    private InvestmentRateService investmentRateService;

    private Locale locale = LocaleContextHolder.getLocale();

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public List<FixedDepositDTO> getFixedDepositDetials(String username) {
        List<FixedDepositDTO> fixedDepositDTOS  = new ArrayList<>();
        if(username != null){
        RetailUser retailUser =  retailUserService.getUserByName(username);
        if(retailUser != null) {

            List<Account> accounts = accountService.getAccountByCifIdAndSchemeType(retailUser.getCustomerId(), "FIXED DEPOSIT");
            logger.info("the size of the accounts {}",accounts.size());
            accounts.forEach(i->{
                FixedDepositDTO deposit = integrationService.getFixedDepositDetails(i.getAccountNumber());
                fixedDepositDTOS.add(deposit);
            });


        }
        }
        return fixedDepositDTOS;
    }

    @Override
    public Page<FixedDepositDTO> getFixedDepositDetials(String cifId, Pageable pageable) throws InternetBankingException {

        List<FixedDepositDTO> fixedDepositDTOS  = new ArrayList<>();
        if(cifId != null){
//            RetailUser retailUser =  retailUserService.getUserByName(username);
//            if(retailUser != null) {
//                List<FixedDepositDTO> deposit = integrationService.getFixedDepositDetailsForAccount(retailUser.getCustomerId());
//                    deposit.forEach(j->fixedDepositDTOS.add(j));
                List<Account> accounts = accountService.getAccountByCifIdAndSchemeType(cifId, "FIXED DEPOSIT");
                logger.info("the size of the accounts {}",accounts.size());
            logger.info("the size of the accounts {}",accounts);
                accounts.forEach(i->{
//                    List<FixedDepositDTO> deposit = integrationService.getFixedDepositDetailsForAccount(i.getAccountNumber());
//                    deposit.forEach(j->fixedDepositDTOS.add(j));
                     FixedDepositDTO deposit = integrationService.getFixedDepositDetails(i.getAccountNumber());
                    fixedDepositDTOS.add(deposit);
                });


//            }
        }
        Long totalCount = Long.valueOf(fixedDepositDTOS.size());
        return new PageImpl<FixedDepositDTO>(fixedDepositDTOS,pageable,totalCount);
    }

    @Override
    public Response liquidateDeposit(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {
        if(fixedDepositDTO.getLiquidateType().equalsIgnoreCase("P")) {
            fixedDepositDTO.setAction(FIxedDepositActions.PART_LIQUIDATE.toString());
        }else {
            fixedDepositDTO.setAction(FIxedDepositActions.FULL_LIQUIDATE.toString());
        }
        sendMail(fixedDepositDTO);
           Response response =  integrationService.liquidateFixDeposit(fixedDepositDTO);
           return response;
    }
    @Override
    public Response addFund(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {
        fixedDepositDTO.setAction(FIxedDepositActions.ADD_FUND.toString());
           Response response =  integrationService.addFundToDeposit(fixedDepositDTO);
        sendMail(fixedDepositDTO);
           return response;
    }
    @Override
    public Response bookFixDeposit(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {
        fixedDepositDTO.setAction(FIxedDepositActions.INITIATE.toString());
           Response response =  integrationService.bookFixDeposit(fixedDepositDTO);
           return response;
    }

    @Override
    public boolean isBalanceEnoughForBooking(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {

        BigDecimal availableBalance = integrationService.getAvailableBalance(fixedDepositDTO.getAccountNumber());
        BigDecimal deposit = new BigDecimal(fixedDepositDTO.getInitialDepositAmount());
        int comparator = availableBalance.compareTo(deposit);
        logger.info("the comparator {}",comparator);
        if(comparator > 0){
            return true;
        }
        return false;
    }

    @Override
    public String sendMail(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {
        User currentUser = getCurrentUser();
        if(currentUser != null) {

            Context context = context(fixedDepositDTO);
            context.setVariable("username",currentUser.getUserName());
            sendMails(context);
        }
        return null;
    }


    private User getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal !=null) {
            return principal.getUser();
        }else {
            return null;
        }
    }
    private String getFullName(User user) {

        String firstName = "";
        String lastName = "";
        if (user.getLastName() != null) {
            lastName = user.getLastName();
        }
        if (user.getFirstName() == null) {
            firstName = user.getFirstName();
        }
        String name = firstName + ' ' + lastName;
        return name;
    }
    private Context context(FixedDepositDTO fixedDepositDTO){
        Context context = new Context();
        if(fixedDepositDTO.getAction().contains("LIQUIDATE")){
            String alertSubject = String.format(messageSource.getMessage("deposit.liquidate.subject", null, locale));
            if(fixedDepositDTO.getLiquidateType().equalsIgnoreCase("P")) {
                context.setVariable("liquidateType", "Part Liquidation");
            }else {
                context.setVariable("liquidateType", "Full Liquidation");
            }
            context.setVariable("amount", fixedDepositDTO.getInitialDepositAmount());
            context.setVariable("accountNumber", fixedDepositDTO.getAccountNumber());
            context.setVariable("alertSubject",alertSubject);
            context.setVariable("template","mail/liquidate.html");
        }else if(fixedDepositDTO.getAction().equalsIgnoreCase("ADD_FUND")) {
            String alertSubject = String.format(messageSource.getMessage("deposit.add.fund.subject", null, locale));
            context.setVariable("accountNumber", fixedDepositDTO.getAccountNumber());
            context.setVariable("amount" ,fixedDepositDTO.getInitialDepositAmount());
            context.setVariable("alertSubject",alertSubject);
            context.setVariable("template","mail/addFund.html");
        }

        return context;
    }
    private void sendMails(Context context ){
        UserGroup userGroup = userGroupRepo.findByNameIgnoreCase("Customer care");
        logger.info("the user group {}",userGroup);
        if(null != userGroup) {
            List<ContactDTO> contacts = groupService.getContacts(userGroup);
            if (contacts.size() > 0) {
                for (ContactDTO contact : contacts) {
                    String fullName = contact.getFirstName() + " " + contact.getLastName();
                    context.setVariable("fullName", fullName);
                    Email email = new Email.Builder().setRecipient(contact.getEmail())
                            .setSubject(context.getVariable("alertSubject").toString())
                            .setTemplate(context.getVariable("template").toString())
                            .build();
                    mailService.sendMail(email, context);
                }
            }
        }

    }

    @Override
    public Optional<Integer> getRateBasedOnAmountAndTenor(int amount, int tenor)    {
        logger.info(tenor+"amount {}",amount);

        return investmentRateService.getRateByTenorAndAmount("FIXED-DEPOSIT",tenor,amount);
    }



}
