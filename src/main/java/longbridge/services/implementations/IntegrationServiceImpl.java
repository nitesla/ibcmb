package longbridge.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.*;
import longbridge.exception.InternetBankingTransferException;

import longbridge.models.TransRequest;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import longbridge.utils.AccountStatement;
import longbridge.utils.ResultType;
import longbridge.utils.TransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Fortune on 4/4/2017.
 * Modified by Farooq
 */

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${ebank.service.uri}")
    private String URI;
    @Value("${CMB.ALERT.URL}")
    private String cmbAlert;

    private RestTemplate template;
    private MailService mailService;
    private TemplateEngine templateEngine;
    @Autowired
    public IntegrationServiceImpl(RestTemplate template,MailService mailService,TemplateEngine templateEngine) {
        this.template = template;
        this.mailService=mailService;
        this.templateEngine=templateEngine;
    }


    @Override
    public AccountInfo fetchAccount(String accountNumber) {
        //TODO
        AccountInfo accountInfo = new AccountInfo();
        return accountInfo;
    }

    @Override
    public List<AccountInfo> fetchAccounts(String cifid) {
        try {

            String uri = URI + "/customer/{acctId}/accounts";


            return Arrays.stream(template.getForObject(uri, AccountInfo[].class, cifid)).collect(Collectors.toList());
            //            List list= template.getForObject(uri, ArrayList.class,cifid);

        } catch (Exception e) {
            logger.error("Exception occurred {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public AccountStatement getAccountStatements(String accountId, Date fromDate, Date toDate) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> getBalance(String accountId) {
        String uri = URI + "/account/{acctId}";
        Map<String, String> params = new HashMap<>();
        params.put("acctId", accountId);
        Map<String, BigDecimal> response = new HashMap<>();
        try {
            AccountDetails details = template.getForObject(uri, AccountDetails.class, params);

            BigDecimal availBal = new BigDecimal(details.getAvailableBalance());
            BigDecimal ledgBal = new BigDecimal(details.getLedgerBalAmt());
            response.put("AvailableBalance", availBal);
            response.put("LedgerBalance", ledgBal);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TransRequest makeTransfer(TransRequest transRequest) throws InternetBankingTransferException {

        TransferType type = transRequest.getTransferType();

        //switch (type) {
        switch (type) {
            case CORONATION_BANK_TRANSFER:

            {
                transRequest.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
                TransferDetails response;
                String uri = URI + "/transfer/local";
                Map<String, String> params = new HashMap<>();
                params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
                params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
                params.put("tranAmount", transRequest.getAmount().toString());
                params.put("naration", transRequest.getNarration());
                logger.info(params.toString());

                try {
                    response = template.postForObject(uri, params, TransferDetails.class);

                    if (response != null) {
                        transRequest.setStatus(response.getResponseDescription());
                        transRequest.setReferenceNumber(response.getUniqueReferenceCode());
                        transRequest.setNarration(response.getNarration());

                        return transRequest;
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    transRequest.setStatus(ResultType.ERROR.toString());
                    return transRequest;

                }


            }
            case INTER_BANK_TRANSFER: {
                transRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
                TransferDetails response;
                String uri = URI + "/transfer/nip";
                Map<String, String> params = new HashMap<>();
                params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
                params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
                params.put("tranAmount", transRequest.getAmount().toString());
                params.put("destinationInstitutionCode", transRequest.getFinancialInstitution().getInstitutionCode());
                params.put("tranType", "NIP");
                logger.info("params for transfer {}", params.toString());
                try {
                    response = template.postForObject(uri, params, TransferDetails.class);
                    if (response != null) {
                        logger.info("response for transfer {}", response.toString());
                        transRequest.setReferenceNumber(response.getUniqueReferenceCode());
                        transRequest.setNarration(response.getNarration());
                        transRequest.setStatus(response.getResponseDescription());

                        return transRequest;


                    }


                } catch (Exception e) {

                    e.printStackTrace();
                    transRequest.setStatus(ResultType.ERROR.toString());
                    return transRequest;
                }


            }
            case INTERNATIONAL_TRANSFER: {

            }
            case NAPS: {

            }
            case OWN_ACCOUNT_TRANSFER: {
                transRequest.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
                TransferDetails response = null;
                String uri = URI + "/transfer/local";
                Map<String, String> params = new HashMap<>();
                params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
                params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
                params.put("tranAmount", transRequest.getAmount().toString());
                params.put("naration", transRequest.getNarration());
                logger.info("patrams for transfer {}", params.toString());
                try {
                    response = template.postForObject(uri, params, TransferDetails.class);
                    System.out.println("@@@ RESPONSE " + response);
                    if (response != null) {
                        transRequest.setNarration(response.getNarration());
                        transRequest.setReferenceNumber(response.getUniqueReferenceCode());
                        System.out.println(response.getResponseDescription());
                        transRequest.setStatus(response.getResponseDescription());
                        return transRequest;
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    transRequest.setStatus(ResultType.ERROR.toString());
                    return transRequest;
                }


            }

            case RTGS: {

            }
        }


        logger.trace("request did not match any type");
        transRequest.setStatus(ResultType.ERROR.toString());
        return transRequest;
    }

    @Override
    public AccountDetails viewAccountDetails(String acctNo) {

        String uri = URI + "/account/{acctId}";
        Map<String, String> params = new HashMap<>();
        params.put("acctId", acctNo);
        try {
            AccountDetails details = template.getForObject(uri, AccountDetails.class, params);
            return details;
        } catch (Exception e) {
            return null;
        }


    }

    @Override
    public CustomerDetails isAccountValid(String accNo, String email, String dob) {
        CustomerDetails result = null;
        String uri = URI + "/account/verification";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber", accNo);
        params.put("email", email);
        params.put("dateOfBirth", dob);
        try {
            result = template.postForObject(uri, params, CustomerDetails.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String getAccountName(String accountNumber) {
        logger.info(accountNumber + "account number");

        return viewAccountDetails(accountNumber).getAcctName();

    }

    @Override
    public BigDecimal getDailyDebitTransaction(String acctNo) {

        BigDecimal result = null;
        String uri = URI + "/transfer/dailyTransaction";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber", acctNo);

        try {
            String response = template.postForObject(uri, params, String.class);
            result = new BigDecimal(response);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;

    }


    @Override
    public BigDecimal getDailyAccountLimit(String accNo, String channel) {
        BigDecimal result = null;
        String uri = URI + "/transfer/limit";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber", accNo);
        params.put("transactionChannel", channel);
        try {
            String response = template.postForObject(uri, params, String.class);
            result = new BigDecimal(response);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;

    }

    @Override
    public NEnquiryDetails doNameEnquiry(String destinationInstitutionCode, String accountNumber) {
        NEnquiryDetails result = null;
        String uri = URI + "/transfer/nameEnquiry";
        Map<String, String> params = new HashMap<>();
        params.put("destinationInstitutionCode", destinationInstitutionCode);
        params.put("accountNumber", accountNumber);
        logger.trace("params {}", params);
        try {

            result = template.postForObject(uri, params, NEnquiryDetails.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public BigDecimal getAvailableBalance(String s) {
        Map<String, BigDecimal> getBalance = getBalance(s);
        BigDecimal balance = getBalance.get("AvailableBalance");
        if (balance != null) {
            return balance;
        }
        return new BigDecimal(0);
    }

    @Override
    public ObjectNode sendSMS(String message, String contact, String subject) {
        List<String> contacts = new ArrayList<>();
        contacts.add(contact);

        ObjectNode result = null;
        String uri = cmbAlert;
        Map<String, Object> params = new HashMap<>();
        params.put("alertType", "SMS");
        params.put("message", message);
        params.put("subject", subject);
        params.put("contactList", contacts);
        logger.trace("params {}", params);

        try {

            result = template.postForObject(uri, params, ObjectNode.class);
        } catch (Exception e) {
            e.printStackTrace();


        }

        return result;
    }

    public TransRequest sendTransfer(TransRequest transRequest){

        Context scontext = new Context();
        scontext.setVariable("uploadDate",new Date());

//        String smail = templateEngine.process("smail", scontext);

      //  mailService.send();
        return transRequest;
    }


}

