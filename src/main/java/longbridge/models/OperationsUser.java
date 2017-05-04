package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Wunmi on 3/28/2017.
 *  OperationsUser is a Staff of the bank tasked with managing customers. Tellers and Customer relationship
 *  manager fall into this group.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class OperationsUser extends User {
	public OperationsUser(){
		this.userType = (UserType.OPERATIONS);
	}

	@ManyToOne
	private UserGroup userGroup;

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Override
	public String toString() {
		return "OperationsUser{"+super.toString()+"}";
	}

}
