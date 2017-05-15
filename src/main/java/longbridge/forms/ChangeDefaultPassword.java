package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 5/15/2017.
 */
public class ChangeDefaultPassword {

    @NotEmpty(message = "newPassword")
    private String newPassword;
    @NotEmpty(message = "confirmPassword")
    private String confirmPassword;


    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}

