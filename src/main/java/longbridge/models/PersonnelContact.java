package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Fortune on 4/26/2017.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class PersonnelContact extends AbstractEntity{

    private String name;
    private String email;

    @ManyToOne
    private Unit unit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "UnitPersonnel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
