package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class UserGroup extends AbstractEntity {
<<<<<<< HEAD

    private String name;

//    @ManyToMany(mappedBy = "user")
//    private Collection<User> users;

<<<<<<< HEAD
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
=======
=======

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_userGroup", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "group_id", referencedColumnName = "Id"))
    private Collection<User> users;
>>>>>>> OLUGINGIN

>>>>>>> 21972ffea0b84236dd74742d57bac1d69c5b614a
}
