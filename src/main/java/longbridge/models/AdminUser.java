package longbridge.models;

import javax.persistence.Entity;

/**
 *
 */
@Entity
public class AdminUser extends User {


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
