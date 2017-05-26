package longbridge.api;

/**
 * Created by ayoade_farooq@yahoo.com on 4/27/2017.
 */
public class NEnquiryDetails
{

    private String responseCode;
    private String responseDescription;
    private String accountName;
    private String customerBVN;
    private String  customerKYC;


    public NEnquiryDetails() {
    }


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCustomerBVN() {
        return customerBVN;
    }

    public void setCustomerBVN(String customerBVN) {
        this.customerBVN = customerBVN;
    }

    public String getCustomerKYC() {
        return customerKYC;
    }

    public void setCustomerKYC(String customerKYC) {
        this.customerKYC = customerKYC;
    }

    @Override
    public String toString() {
        return "NEnquiryDetails{" +
                "responseCode='" + responseCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", accountName='" + accountName + '\'' +
                ", customerBVN='" + customerBVN + '\'' +
                ", customerKYC='" + customerKYC + '\'' +
                '}';
    }
}
