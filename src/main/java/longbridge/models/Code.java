package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Fortune on 3/29/2017.
 */

@Entity
public class Code extends AbstractEntity {

    private String code;
    private String type;
    private String description;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Code other = (Code) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Code [id=" + id + ", code=" + code + ", description="
                + description + ", type=" + type + "]";
    }

}
