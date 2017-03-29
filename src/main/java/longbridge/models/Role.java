package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Role extends AbstractEntity{
<<<<<<< HEAD
=======

>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae

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
}
