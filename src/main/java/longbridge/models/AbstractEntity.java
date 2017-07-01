package longbridge.models;


import org.hibernate.annotations.Where;
import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * The {@code AbstractEntity} abstract class is a superclass for all entities.
 * Entities that are subclasses of this {@code AbstractEntity} inherits the variables and methods defined here
 * @author Fortunatus Ekenachi
 */


@MappedSuperclass
public abstract class AbstractEntity implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -2361696892354119780L;

	@javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Version
    protected int version;

    protected String delFlag = "N";

    protected Date deletedOn;

//    pr
// otected Date lastModificationTime;
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

    public Date getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Date deletedOn) {
        this.deletedOn = deletedOn;
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
    
	
    @Override
    public String toString() {
        return
                "id=" + id +
                ", version=" + version +
                ", delFlag='" + delFlag + '\'' +
                ", deletedOn=" + deletedOn +
                '}';
    }
}
