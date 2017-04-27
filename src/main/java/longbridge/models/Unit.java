package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by Fortune on 4/26/2017.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class Unit extends AbstractEntity{

    private String code;
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<PersonnelContact> personnel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<PersonnelContact> getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Collection<PersonnelContact> personnel) {
        this.personnel = personnel;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", personnel=" + personnel +
                '}';
    }
}
