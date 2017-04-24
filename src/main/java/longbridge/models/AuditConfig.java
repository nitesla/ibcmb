package longbridge.models;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;

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
//	@Override
//	public String serialize() throws JsonProcessingException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public AbstractEntity deserialize(String data) throws JsonParseException, JsonMappingException, IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	@Override
	public OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
