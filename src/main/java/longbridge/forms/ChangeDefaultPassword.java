package longbridge.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Created by Fortune on 5/15/2017.
 */
public class ChangeDefaultPassword {

    @NotEmpty(message = "Enter the New Password")
    private String newPassword;
    @NotEmpty(message = "Confirm the New Password")
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

