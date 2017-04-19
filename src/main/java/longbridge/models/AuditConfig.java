package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */

@Entity
@Where(clause ="del_Flag='N'" )
public class AuditConfig extends AbstractEntity
{
    @Column(name = "table_name")
    private String entityName;


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

}
