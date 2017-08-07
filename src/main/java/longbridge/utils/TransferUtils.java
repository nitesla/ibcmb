package longbridge.utils;

import longbridge.api.NEnquiryDetails;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferExceptions;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountService;
import longbridge.services.CorporateService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 7/10/2017.
 */
@Service
public class TransferUtils {

    private IntegrationService integrationService;
    private AccountService accountService;
    private RetailUserService retailUserService;
    private CorporateService corporateService;

    @Autowired
    public void setCorporateService(CorporateService corporateService) {
        this.corporateService = corporateService;
    }

    @Autowired
    public void setRetailUserService(RetailUserService retailUserService) {
        this.retailUserService = retailUserService;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setIntegrationService(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    private String createMessage(String message, boolean successOrFailure) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", message);
            object.put("success", successOrFailure);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }


    public String doIntraBankkNameLookup(String acctNo) {
        if (getCurrentUser() != null) {
            String name = integrationService.viewAccountDetails(acctNo).getAcctName();
            if (name != null && !name.isEmpty()) return createMessage(name, true);

            return createMessage("Invalid Account", false);

        }

        return "";
    }

    public String doInterBankNameLookup(String bank, String accountNo) {

        if (getCurrentUser() != null) {

            NEnquiryDetails details = integrationService.doNameEnquiry(bank, accountNo);
            if (details == null)
                return createMessage("service down please try later", false);


            if (details.getResponseCode() != null && !details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getResponseDescription(), false);


            if (details.getAccountName() != null && details.getResponseCode() != null && details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getAccountName(), true);


            return createMessage("session expired", false);

        }
        return "";
    }


    public String getBalance(String accountNumber) {
        if (getCurrentUser() != null) {
            Account account = accountService.getAccountByAccountNumber(accountNumber);
            Map<String, BigDecimal> balance = accountService.getBalance(account);
            BigDecimal availBal = balance.get("AvailableBalance");
            return createMessage(availBal.toString(), true);
        }
        return "";
    }


    public String getLimit(String accountNumber) {
        if (getCurrentUser() != null) {
            String limit = integrationService.getDailyAccountLimit(accountNumber, "NIP");
            if (limit != null && !limit.isEmpty())
                return createMessage(limit, true);
        }

        return "";
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }

        return null;
    }

    public List<Account> getNairaAccounts() {

        RetailUser user = retailUserService.getUserByName(getCurrentUser().getUserName());
        List<Account> accountList = new ArrayList<>();
        if (user != null) {


            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))

                    .forEach(i -> accountList.add(i));

        }
        return accountList;
    }


    public List<Account> getNairaAccounts(String custId) {

        List<Account> accountList = new ArrayList<>();
        if (custId != null && !custId.isEmpty()) {


            Iterable<Account> accounts = accountService.getAccountsForDebit(custId);

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))

                    .forEach(i -> accountList.add(i));

        }
        return accountList;
    }

    public String getFee(String channel) {
        try {
            return integrationService.getFee(channel).getFeeValue();
        } catch (Exception e) {
            return null;
        }

    }

    public void validateBvn() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            if (currentUser.getCorpId() == null) {
                RetailUser retailUser = retailUserService.getUserByName(currentUser.getUsername());
                if (retailUser.getBvn() == null || "NO BVN".equalsIgnoreCase(retailUser.getBvn()) || retailUser.getBvn().isEmpty()) {
                  throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());
                }

            } else {
                Corporate corporate = corporateService.getCorp(currentUser.getCorpId());
                if (corporate==null)  throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());

                if (corporate.getBvn()==null || corporate.getBvn().isEmpty() || "NO BVN".equalsIgnoreCase( corporate.getBvn()) ){
                    throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());
                }

            }

            throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());
        }

    }
}
