package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Role extends AbstractEntity{


    private String name;
    private String description;

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

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
