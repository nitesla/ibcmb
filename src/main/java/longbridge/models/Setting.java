package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by LB-PRJ-020 on 4/5/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class Setting extends AbstractEntity{

    @Column(unique = true)
    private String name;

    private String description;
    private String value;
    private boolean isEnabled = true;

    @ManyToOne
    private AdminUser modifiedBy;


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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
                ", isEnabled=" + isEnabled +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}
