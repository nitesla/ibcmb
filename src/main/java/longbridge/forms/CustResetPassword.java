package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ayoade_farooq@yahoo.com on 5/29/2017.
 */
public class CustResetPassword {

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
