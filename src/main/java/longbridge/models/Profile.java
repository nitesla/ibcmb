package longbridge.models;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String name;
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profile_permission", joinColumns =
    @JoinColumn(name = "profile_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "permission_id", referencedColumnName = "Id"))
    private Collection<Permission> permissions;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

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
}
