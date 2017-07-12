package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 5/29/2017.
 */
public class SyncTokenForm {

    private String userType;
    @NotEmpty(message = "username")
    private String username;
    @NotEmpty(message = "serialNumber")
    private String serialNumber;
    @NotEmpty(message = "tokenCode1")
    private String tokenCode1;
    @NotEmpty(message = "tokenCode2")
    private String tokenCode2;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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
