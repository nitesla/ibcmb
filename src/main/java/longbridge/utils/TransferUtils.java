package longbridge.utils;

import longbridge.api.NEnquiryDetails;
import longbridge.services.IntegrationService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 7/10/2017.
 */
@Service
public class TransferUtils {

    private IntegrationService integrationService;

    @Autowired
    public void setIntegrationService(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    private String createMessage(String message, boolean successOrFailure) {
        JSONObject object = new JSONObject();
        //ObjectNode object = Json.newObject();
        try {
            object.put("message", message);
            object.put("success", successOrFailure);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }


    public String doIntraBankkNameLookup(String acctNo){
        String name = integrationService.viewAccountDetails(acctNo).getAcctName();
        if (name!=null && !name.isEmpty()) return createMessage(name,true);

        return createMessage("Invalid Account",false);


    }

    public String doInterBankNameLookup(String bank,String accountNo){

        NEnquiryDetails details = integrationService.doNameEnquiry(bank, accountNo);
        if (details == null)
            return createMessage("service down please try later", false);


        if (details.getResponseCode() != null && !details.getResponseCode().equalsIgnoreCase("00"))
            return createMessage(details.getResponseDescription(), false);


        if (details.getAccountName() != null && details.getResponseCode() != null && details.getResponseCode().equalsIgnoreCase("00"))
            return createMessage(details.getAccountName(), true);


        return createMessage("session expired", false);
    }





}
