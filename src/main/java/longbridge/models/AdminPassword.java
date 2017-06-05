package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Fortune on 6/5/2017.
 */
@Entity
public class AdminPassword extends AbstractEntity {

    AdminUser adminUser;
    String password;

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
