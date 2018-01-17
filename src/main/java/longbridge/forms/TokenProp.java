package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Showboy on 28/05/2017.
 */
public class TokenProp {

    @NotEmpty(message = "Please Select a Token device")
    private String serialNo;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
