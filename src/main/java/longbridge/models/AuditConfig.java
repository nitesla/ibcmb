package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */

@Entity
@Where(clause ="del_flag='N'" )
public class AuditConfig extends AbstractEntity
{
    @Column(name = "table_name")
    private String entityName;
    private String enabled;
	private String fullName;
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public List<String> getDefaultSearchFields()
	{
		return Collections.singletonList("entityName");
	}





}
