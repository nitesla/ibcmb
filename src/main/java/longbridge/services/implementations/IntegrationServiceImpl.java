package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;

import longbridge.models.TransferRequest;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import longbridge.utils.TransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Fortune on 4/4/2017.
 */

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private Logger  logger= LoggerFactory.getLogger(getClass());

    @Value("${ebank.service.uri}")
    private   String URI;   //TODO URI for the account details class

    private RestTemplate template;


    public IntegrationServiceImpl() {
    }

     @Autowired
    public IntegrationServiceImpl(RestTemplate template) {
        this.template = template;
    }

    @Override
    public Collection<AccountInfo> fetchAccounts(String cifid)
    {
        try{

            String uri=URI +"/customer/{acctId}/accounts";

            List<AccountInfo> details= template.getForObject(uri, ArrayList.class,cifid);
            return details;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public AccountStatement getAccountStatements(String accountId, Date fromDate, Date toDate) {
        return null;
    }

    @Override
    public  Map<String, BigDecimal> getBalance(String accountId) {
        String uri=URI +"/account/{acctId}";
        Map<String, String> params = new HashMap<>();
        params.put("acctId",accountId );
        Map<String, BigDecimal> response= new HashMap<>();
        try{
            AccountDetails details= template.getForObject(uri,AccountDetails.class,params);

            BigDecimal availBal= new BigDecimal(details.getAvailableBalance());
            BigDecimal ledgBal= new BigDecimal(details.getLedgerBalAmt());
            response.put("AvailableBalance",availBal);
            response.put("LedgerBalance",ledgBal);


            return response;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void makeTransfer(TransferRequest transferRequest) {

        TransferType type;
        type = TransferType.INTER_BANK_TRANSFER;
 switch (type){
     case CORONATION_BANK_TRANSFER:

     {
//template.postForObject();
     }




 }


    }

    @Override
    public AccountDetails viewAccountDetails(String acctNo) {

        String uri=URI +"/account/{acctId}";
        Map<String, String> params = new HashMap<>();
        params.put("acctId",acctNo );
        try{
            AccountDetails details= template.getForObject(uri,AccountDetails.class,params);
            return details;
        }catch (Exception e){
            return null;
        }


    }

    @Override
    public Boolean isAccountValid(String accNo,String email,String dob) {
       boolean result=false;
        String uri=URI +"/account/verification";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber",accNo );
        params.put("email",email );
        params.put("dateOfBirth",dob );
        try {
           result = template.postForObject(uri,params,Boolean.class);

        }
        catch (Exception e){

            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String getAccountName(String accountNumber) {
    	logger.info(accountNumber + "account number");
        if("0021424028".equals(accountNumber.trim())){
            return "Torti Chigozirim David";
        }
        return accountNumber;
    }

    @Override
    public BigDecimal getDailyDebitTransaction(String acctNo) {

        BigDecimal result =null;
        String uri=URI +"/transfer/dailyTransaction";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber",acctNo );

        try {
            String response    = template.postForObject(uri,params,String.class);
            result = new BigDecimal(response);
        }
        catch (Exception e){

            e.printStackTrace();
        }

        return result;

    }





    @Override
    public BigDecimal getDailyAccountLimit(String accNo,String channel ) {
       BigDecimal result =null;
        String uri=URI +"/transfer/limit";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber",accNo );
        params.put("transactionChannel",channel );
        try {
         String response    = template.postForObject(uri,params,String.class);
        result = new BigDecimal(response);
        }
        catch (Exception e){

            e.printStackTrace();
        }

        return result;

    }
}
