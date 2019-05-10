package longbridge.api;



/**
 * Created by ayoade_farooq@yahoo.com on 6/14/2017.
 */


public class Naps {


    public Naps() {
    }

    public Naps(String filePath, String batchId, String customerAccount, String customerAccountName, String remarks, String customerEmail) {

        this.batchId = batchId;
        this.customerAccount = customerAccount;
        this.customerAccountName = customerAccountName;
        this.remarks = remarks;
        this.customerEmail = customerEmail;
        this.filePath=filePath;
    }


    private String  batchId;
    private String customerAccount;
    private String customerAccountName;
    private String remarks;
    private String customerEmail;
    private String filePath;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public String getCustomerAccountName() {
        return customerAccountName;
    }

    public void setCustomerAccountName(String customerAccountName) {
        this.customerAccountName = customerAccountName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
