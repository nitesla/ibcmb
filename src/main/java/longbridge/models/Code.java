package longbridge.models;

import javax.persistence.Entity;

/**
 * The {@code Code} class model represents unique data that can be used for system configurations.
 * This can be used to set up a list of items that can be presented in drop-down menus
 * Example is a country eg Nigeria with code: NG, type: COUNTRY and description: the description if necessary
 * With this approach, new items can be added or removed from a list presented to the user
 * @author Fortunatus Ekenachi
 * Created on 3/29/2017.
 *
 */

@Entity
public class Code extends AbstractEntity {

    private String code;
    private String type;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Code code1 = (Code) o;

        if (code != null ? !code.equals(code1.code) : code1.code != null) return false;
        return type != null ? type.equals(code1.type) : code1.type == null;
    }

    @Override
    public String toString() {
        return "Code [id=" + id + ", code=" + code + ", description="
                + description + ", type=" + type + "]";
    }

    
}
