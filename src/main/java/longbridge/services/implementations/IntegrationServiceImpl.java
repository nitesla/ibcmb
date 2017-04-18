package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.models.Account;
import longbridge.models.TransferRequest;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fortune on 4/4/2017.
 */

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private RestTemplate template;


    public IntegrationServiceImpl() {
    }

//     @Autowired
//    public IntegrationServiceImpl(RestTemplate template) {
//        this.template = template;
//    }

    @Override
    public Iterable<Account> fetchAccounts(String cifid) {
        return null;
    }

    @Override
    public AccountStatement getAccountStatements(String accountId, Date fromDate, Date toDate) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> getBalance(String accountId) {
        return null;
    }

    @Override
    public void makeTransfer(TransferRequest transferRequest) {


    }

    @Override
    public AccountDetails viewAccountDetails(String acctNo) {
        String uri="";//TODO URI for the account details class
        Map<String, String> params = new HashMap<>();
        params.put("acctId",acctNo );
        try{
            AccountDetails details= template.getForObject(uri,AccountDetails.class,params);
            return details;
        }catch (Exception e){
            return null;
        }


    }
}
