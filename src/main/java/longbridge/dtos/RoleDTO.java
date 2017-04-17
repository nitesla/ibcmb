package longbridge.dtos;

import longbridge.models.AbstractEntity;
import longbridge.models.Permission;
import longbridge.models.UserType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by Fortune on 4/5/2017.
 */
public class RoleDTO{

    private Long id;
   @NotEmpty
    private String name;
   @NotEmpty
    private String description;
    private String email;

    private UserType userType;

    private Collection<PermissionDTO> permissions;

    public Collection<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "RoleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", userType=" + userType +
                ", permissions=" + permissions +
                '}';
    }
}
