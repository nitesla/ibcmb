package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 3/28/2017.
 *  OperationsUser is a Staff of the bank tasked with managing customers. Tellers and Customer relationship
 *  manager fall into this group.
 */

@Entity
@Audited
public class OperationsUser extends User {
	public OperationsUser(){
		this.userType = (UserType.OPERATIONS);
	}
	@Override
	public String toString() {
		return "OperationsUser{"+super.toString()+"}";
	}

}
