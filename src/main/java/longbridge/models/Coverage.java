package longbridge.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(name = "coverage", uniqueConstraints =
@UniqueConstraint(columnNames = {"eid", "type", "code"}))
public class Coverage extends AbstractEntity  {


    private boolean enabled;
    private String customerId;
    @Embedded
    private EntityId entityId;
    private String code;

    @ManyToOne
    @JoinColumn(name = "code_id",referencedColumnName = "id")
    private Code codeEntity;




    public Coverage() {
    }

    public Code getCodeEntity() {
        return codeEntity;
    }

    public void setCodeEntity(Code codeEntity) {
        this.codeEntity = codeEntity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public EntityId getEntityId() {
        return entityId;
    }

    public void setEntityId(EntityId entityId) {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
