package longbridge.models;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Profile extends AbstractEntity{




    private String name;
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profile_permission", joinColumns =
    @JoinColumn(name = "profile_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "permission_id", referencedColumnName = "Id"))
    private Collection<Permission> permissions;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
