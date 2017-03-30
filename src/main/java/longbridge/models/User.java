package longbridge.models;

/**
 * Created by Showboy on 29/03/2017.
 */
public interface User{

    String getUserName();

    void setUserName(String userName);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Role getRole();

    void setRole(Role role);

    Profile getProfile();

    void setProfile(Profile profile);

    String toString();


}
