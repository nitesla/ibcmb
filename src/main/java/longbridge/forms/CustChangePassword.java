package longbridge.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Created by Showboy on 25/05/2017.
 */
public class CustChangePassword {
    @NotEmpty(message = "Enter the Old Password")
    private String oldPassword;
    @NotEmpty(message = "Enter the New Password")
    private String newPassword;
    @NotEmpty(message = "Confirm the New Password")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

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
