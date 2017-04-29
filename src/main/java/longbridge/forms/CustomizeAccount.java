package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Wunmi Sowunmi on 27/04/2017.
 */
public class CustomizeAccount {

    @NotEmpty
    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
