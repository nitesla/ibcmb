package longbridge.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Created by Wunmi Sowunmi on 25/04/2017.
 */
public class AlertPref {

    @NotEmpty
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
