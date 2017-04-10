package longbridge.models;


import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The {@code AbstractEntity} abstract class is a superclass for all entities.
 * Entities that are subclasses of this {@code AbstractEntity} inherits the variables and methods defined here
 * @author Fortunatus Ekenachi
 */


@MappedSuperclass
public abstract class AbstractEntity implements Serializable{

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Version
    protected int version;

    protected String delFlag = "N";

//    protected Date lastModificationTime;
//
//    protected Date createdTime = Date.now();
//
//    protected String createdBy;
//
//    protected String modifiedBy;
//todo envers implementation

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

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
        AbstractEntity other = (AbstractEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
