package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import java.util.List;

import javax.persistence.*;

/**
 * Created by Wunmi on 3/28/2017.
 *  OperationsUser is a Staff of the bank tasked with managing customers. Tellers and Customer relationship
 *  manager fall into this group.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))
public class OperationsUser extends User implements Person {
	public OperationsUser(){
		this.userType = (UserType.OPERATIONS);
	}

	@ManyToMany(mappedBy = "users")
	private List<UserGroup> groups;





	public List<UserGroup> getGroups() {
		return groups;
	}




	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}




	@Override
	public String toString() {
		return "OperationsUser{"+super.toString()+"}";
	}



	@Override
	public boolean isExternal() {
		return false;
	}


}
