package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by LB-PRJ-020 on 4/5/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"name","deletedOn"}))

public class Setting extends AbstractEntity{

    private String name;

    private String type;
    private String description;
    private String value;
    private boolean enabled ;

    @ManyToOne
    private AdminUser modifiedBy;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


	public AdminUser getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(AdminUser modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                ", enabled=" + enabled +
                ", modifiedBy=" + modifiedBy +
                '}';
    }

	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
