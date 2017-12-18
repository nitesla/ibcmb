package longbridge.api.omnichannel.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Fortune on 12/18/2017.
 */
public class RetailUserCredentials {

    private String username;
    private String password;

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

    @Override
    public String toString() {
        return "RetailUserCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
