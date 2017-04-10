package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(
		name="code",
		uniqueConstraints=
		@UniqueConstraint(columnNames={"code", "type"})
)
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Code other = (Code) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Code{" +
				"code='" + code + '\'' +
				", type='" + type + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
