package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Created by Fortune on 6/5/2017.
 */

@Entity
@Where(clause ="del_Flag='N'" )
public class RetailPassword extends AbstractEntity{

    String username;
    String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
