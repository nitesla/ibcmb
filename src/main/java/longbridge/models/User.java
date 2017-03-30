package longbridge.models;

<<<<<<< HEAD
import javax.persistence.*;
import java.util.Collection;
=======
<<<<<<< HEAD
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
>>>>>>> 21972ffea0b84236dd74742d57bac1d69c5b614a

/**
 * Created by Showboy on 29/03/2017.
 */
public abstract class User extends AbstractEntity {


    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_groups", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "group_id", referencedColumnName = "Id"))
    private Collection<UserGroup> userGroups;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
=======
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


>>>>>>> OLUGINGIN
}
