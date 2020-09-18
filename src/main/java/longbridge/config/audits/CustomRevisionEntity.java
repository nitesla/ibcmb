package longbridge.config.audits;


import longbridge.config.audits.listeners.EntityRevisionListener;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
@Entity
@RevisionEntity(EntityRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity implements Serializable
{

	private static final long serialVersionUID = 1767924677148716529L;
	private String lastChangedBy;
	private String ipAddress;


    public CustomRevisionEntity() {
        super();
    }

    public String getLastChangedBy() {
        return lastChangedBy;
    }

    public void setLastChangedBy(String lastChangedBy) {
        this.lastChangedBy = lastChangedBy;
    }

    public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}



	@OneToMany(mappedBy="revision", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<ModifiedEntityTypeEntity> modifiedEntityTypes = new HashSet<>();

    public void addModifiedEntityType(String entityClassName)
    {
        modifiedEntityTypes.add(new ModifiedEntityTypeEntity(this, entityClassName));
    }


}
