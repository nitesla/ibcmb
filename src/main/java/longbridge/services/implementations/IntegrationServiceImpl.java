package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;

import longbridge.api.CustomerDetails;
import longbridge.api.LocalTransferResponse;
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
import java.util.stream.Collectors;

/**
 * Created by Fortune on 4/4/2017.
 * Modified by Farooq
 */

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private Logger  logger= LoggerFactory.getLogger(getClass());

    @Value("${ebank.service.uri}")
    private   String URI;

    private RestTemplate template;

     @Autowired
    public IntegrationServiceImpl(RestTemplate template) {
        this.template = template;
    }

    @Override
    public Collection<AccountInfo> fetchAccounts(String cifid)
    {
        try{

            String uri=URI +"/customer/{acctId}/accounts";


            return Arrays.stream(template.getForObject(uri, AccountInfo[].class,cifid)).collect(Collectors.toList());
//            List list= template.getForObject(uri, ArrayList.class,cifid);
//
//            ArrayList<AccountInfo>  details= new ArrayList<>();
//            details.add((AccountInfo)list);
//            logger.info("details {}",details);
//            return details;
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
    public boolean makeTransfer(TransferRequest transferRequest) {

        TransferType type;
        type = TransferType.INTER_BANK_TRANSFER;
 switch (type){
     case CORONATION_BANK_TRANSFER:

     {
//template.postForObject();
     }
     case INTER_BANK_TRANSFER:{

     }
     case INTERNATIONAL_TRANSFER:{

     }
     case NAPS:{

     }
     case OWN_ACCOUNT_TRANSFER:{

         LocalTransferResponse response=null;
         String uri=URI +"/transfer/local";
         Map<String, String> params = new HashMap<>();
         params.put("debitAccountNumber",transferRequest.getCustomerAccountNumber() );
         params.put("creditAccountNumber",transferRequest.getBeneficiaryAccountNumber());
         params.put("tranAmount",transferRequest.getAmount().toString());
         params.put("naration",transferRequest.getNarration());

         try {
             response   = template.postForObject(uri,params,LocalTransferResponse.class);

             if (response.getResponseCode().equalsIgnoreCase("000")){
                 return true;
             }

         }
         catch (Exception e){

             e.printStackTrace();
         }
     return false;

     }

     case RTGS:{

     }
 }

return false;
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
    public CustomerDetails isAccountValid(String accNo, String email, String dob) {
        CustomerDetails result=null;
        String uri=URI +"/account/verification";
        Map<String, String> params = new HashMap<>();
        params.put("accountNumber",accNo );
        params.put("email",email );
        params.put("dateOfBirth",dob );
        try {

           result = template.postForObject(uri,params,CustomerDetails.class);

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

//    @Override
//    public boolean makeLocalTransfer(TransferRequest transferRequest) {
//      if (transferRequest.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER)){
//          LocalTransferResponse response=null;
//          String uri=URI +"/transfer/local";
//          Map<String, String> params = new HashMap<>();
//          params.put("debitAccountNumber",transferRequest.getAccount() );
//          params.put("creditAccountNumber",transferRequest.getBeneficiaryAccountNumber());
//          params.put("tranAmount",transferRequest.getAmount().toString());
//          params.put("naration",transferRequest.getNarration());
//
//          try {
//              response   = template.postForObject(uri,params,LocalTransferResponse.class);
//
//              if (response.getResponseCode().equalsIgnoreCase("000")){
//                  return true;
//              }
//
//          }
//          catch (Exception e){
//
//              e.printStackTrace();
//          }
//
//      }
//      return false;
//    }


	@Override
	public void synchronizeToken(String username) {
		// TODO send request to entrust
		// send request to entrust
		String uri = URI + "/token/synchronize";
		Map<String, String> params = new HashMap<>();
		params.put("username", username);

		try {
			String response = template.postForObject(uri, params, String.class);
		} catch (Exception exc) {
			logger.error("Error", exc);
		}
		// TODO to be implemented
	}

	@Override
	public boolean performTokenValidation(String username, String tokenString) {
		// TODO to be implemented
		// send request to entrust
		String uri = URI + "/token/authenticate";
		Map<String, String> params = new HashMap<>();
		params.put("username", username);
		params.put("token", tokenString);

		try {
			String response = template.postForObject(uri, params, String.class);
		} catch (Exception exc) {
			logger.error("Error", exc);
		}
		// TODO to be implemented
		return true;
	}
}
