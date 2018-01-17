package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 4/3/2017.
 */
public class ChangePassword {

    @NotEmpty(message = "oldPassword")
    private String oldPassword;
    @NotEmpty(message = "newPassword")
    private String newPassword;
    @NotEmpty(message = "confirmPassword")
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
