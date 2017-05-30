package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 5/29/2017.
 */
public class TokenForm {

    @NotEmpty(message = "username")
    private String username;
    @NotEmpty(message = "serialNumber")
    private String serialNumber;

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
}
