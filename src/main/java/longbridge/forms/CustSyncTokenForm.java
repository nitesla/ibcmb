package longbridge.forms;


import javax.validation.constraints.NotEmpty;

/**
 * Created by Showboy on 29/05/2017.
 */
public class CustSyncTokenForm {

    @NotEmpty
    private String serialNo;
    @NotEmpty(message = "Field is required")
    private String tokenCode1;
    @NotEmpty(message = "Field is required")
    private String tokenCode2;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getTokenCode1() {
        return tokenCode1;
    }

    public void setTokenCode1(String tokenCode1) {
        this.tokenCode1 = tokenCode1;
    }

    public String getTokenCode2() {
        return tokenCode2;
    }

    public void setTokenCode2(String tokenCode2) {
        this.tokenCode2 = tokenCode2;
    }

}