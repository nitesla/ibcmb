package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited
public class UserGroup extends AbstractEntity {

    private String name;

//    @ManyToMany(mappedBy = "user")
//    private Collection<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "user_userGroup", joinColumns =
//    @JoinColumn(name = "user_id", referencedColumnName = "Id"), inverseJoinColumns =
//    @JoinColumn(name = "group_id", referencedColumnName = "Id"))
//    private Collection<User> users;

}
