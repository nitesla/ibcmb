package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Permission extends AbstractEntity{


    private String name;
    private String description;
    private String code;



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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
