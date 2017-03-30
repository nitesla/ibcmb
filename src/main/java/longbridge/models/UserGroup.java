package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
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
}
