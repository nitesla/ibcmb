package longbridge.dtos;

import longbridge.models.AbstractEntity;
import longbridge.models.Permission;
import longbridge.models.UserType;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Fortune on 4/5/2017.
 */
public class RoleDTO extends AbstractEntity{

    private String name;
    private String email;
    private String description;

    private UserType userType;

    private Collection<Permission> permissions;

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
