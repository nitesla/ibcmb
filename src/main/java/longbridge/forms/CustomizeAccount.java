package longbridge.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Created by Wunmi Sowunmi on 27/04/2017.
 */
public class CustomizeAccount {

    @NotEmpty (message = "Please enter an Account Name")
    private String preferredName;

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
}
