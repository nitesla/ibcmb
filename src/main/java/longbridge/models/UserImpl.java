package longbridge.models;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * Created by Showboy on 29/03/2017.
 */
public class UserImpl extends User{


    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "role_id", referencedColumnName = "Id"))
    private Role role;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_profile", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "profile_id", referencedColumnName = "Id"))
    private Profile profile;

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public void setUserName(String userName) {

    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public void setFirstName(String firstName) {

    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public void setLastName(String lastName) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public Role getRole() {
        return null;
    }

    @Override
    public void setRole(Role role) {

    }

    @Override
    public Profile getProfile() {
        return null;
    }

    @Override
    public void setProfile(Profile profile) {

    }
}
