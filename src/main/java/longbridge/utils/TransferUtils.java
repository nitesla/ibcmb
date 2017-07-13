package longbridge.utils;

import longbridge.api.NEnquiryDetails;
import longbridge.models.Account;
import longbridge.models.User;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by ayoade_farooq@yahoo.com on 7/10/2017.
 */
@Service
public class TransferUtils {

    private IntegrationService integrationService;
    private AccountService accountService;

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
            if (limit != null || limit.isEmpty())
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

}
