package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import java.io.IOException;
import java.util.List;

import javax.persistence.*;

/**
 * Created by Wunmi on 3/28/2017.
 *  OperationsUser is a Staff of the bank tasked with managing customers. Tellers and Customer relationship
 *  manager fall into this group.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))
public class OperationsUser extends User implements Person,PrettySerializer {
	public OperationsUser(){
		this.userType = (UserType.OPERATIONS);
	}

	@JsonIgnore
	@ManyToMany(mappedBy = "users")
	private List<UserGroup> groups;


	@JsonIgnore
	public List<UserGroup> getGroups() {
		return groups;
	}


	@JsonIgnore
	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}


	@Override
	public String toString() {
		return "OperationsUser{"+super.toString()+"}";
	}



	@Override
	@JsonIgnore
	public boolean isExternal() {
		return false;
	}



}
