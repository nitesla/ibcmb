package longbridge.services.implementations;

import longbridge.dtos.ContactDTO;
import longbridge.dtos.FixedDepositDTO;
import longbridge.dtos.LoanDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.UserGroupRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.FIxedDepositActions;
import longbridge.utils.JasperReport.ReportHelper;
import longbridge.utils.Response;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mac on 08/03/2018.
 */
@Service
public class FixedDepositServiceImpl implements FixedDepositService {

    private final JavaMailSender mailSender;

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

    @Value("${mail.from}")
    private String sender;

    @Value("${jrxmlImage.path}")
    private String imagePath;

    private final Locale locale = LocaleContextHolder.getLocale();

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public FixedDepositServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }





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
        return new PageImpl<>(fixedDepositDTOS, pageable, totalCount);
    }

    @Override
    public Response liquidateDeposit(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {
        if(fixedDepositDTO.getLiquidateType().equalsIgnoreCase("P")) {
            fixedDepositDTO.setAction(FIxedDepositActions.PART_LIQUIDATE.toString());
        }else {
            fixedDepositDTO.setAction(FIxedDepositActions.FULL_LIQUIDATE.toString());
        }
        sendMail(fixedDepositDTO);
        return integrationService.liquidateFixDeposit(fixedDepositDTO);
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
        return integrationService.bookFixDeposit(fixedDepositDTO);
    }

    @Override
    public boolean isBalanceEnoughForBooking(FixedDepositDTO fixedDepositDTO) throws InternetBankingException {

        BigDecimal availableBalance = integrationService.getAvailableBalance(fixedDepositDTO.getAccountNumber());
        BigDecimal deposit = new BigDecimal(fixedDepositDTO.getInitialDepositAmount());
        int comparator = availableBalance.compareTo(deposit);
        logger.info("the comparator {}",comparator);
        return comparator > 0;
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
        return firstName + ' ' + lastName;
    }
    private Context context(FixedDepositDTO fixedDepositDTO){
        Context context = new Context();
        if(fixedDepositDTO.getAction().contains("LIQUIDATE")){
            String alertSubject = messageSource.getMessage("deposit.liquidate.subject", null, locale);
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
            String alertSubject = messageSource.getMessage("deposit.add.fund.subject", null, locale);
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


    @Override
    public FixedDepositDTO getFixedDepositDetails(String accountNumber) {

       return integrationService.getFixedDepositDetails(accountNumber);

    }

    @Override
    public Page<FixedDepositDTO> getFixedDepositForView(String accountNumber, Pageable pageable) throws InternetBankingException {
        List<FixedDepositDTO> fixedDepositDTOS  = new ArrayList<>();
        FixedDepositDTO depositDTO = integrationService.getFixedDepositDetails(accountNumber);
        fixedDepositDTOS.add(depositDTO);
        Long totalCount = Long.valueOf(fixedDepositDTOS.size());
        return new PageImpl<>(fixedDepositDTOS, pageable, totalCount);
    }

    @Override
    public void sendFixedDepositDetails(String recipient, String name, String accountNumber) throws MailException {
        FixedDepositDTO fixedDeposit = integrationService.getFixedDepositDetails(accountNumber);
        MimeMessagePreparator messagePreparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(sender);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(messageSource.getMessage("fixed.deposit.subject", null, locale));
            messageHelper.setText(String.format(messageSource.getMessage("fixed.deposit.message", null, locale),name));
            messageHelper.addAttachment("fixeddeposit_report.pdf", mailAttachmentPdf(fixedDeposit));
        };
        mailSender.send(messagePreparator);

    }

    private DataSource mailAttachmentPdf( FixedDepositDTO fixedDeposit) throws Exception {

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("accountId",fixedDeposit.getAccountId());
        modelMap.put("accountNumber",fixedDeposit.getAccountNumber());
        modelMap.put("bookRefNo",fixedDeposit.getBookRefNo());
        modelMap.put("depositType",fixedDeposit.getDepositType());
        modelMap.put( "despositStatus",fixedDeposit.getDespositStatus());
        modelMap.put("bookingDate",fixedDeposit.getBookingDate());
        modelMap.put("valueDate",fixedDeposit.getValueDate());
        modelMap.put("initialDepositAmount",fixedDeposit.getInitialDepositAmount());
        modelMap.put("maturityDate",fixedDeposit.getMaturityDate());
        modelMap.put("rate",fixedDeposit.getRate());
        modelMap.put("tenor",fixedDeposit.getTenor());
        modelMap.put("maturityAmount",fixedDeposit.getMaturityAmount());
        modelMap.put("logotwo", imagePath);
        List<FixedDepositDTO> fixedDepositDTOList = new ArrayList<>();
        fixedDepositDTOList.add(fixedDeposit);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperReport jasperReport = ReportHelper.getJasperReport("details_report");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(fixedDepositDTOList));
        JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
        return new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");
    }




}
