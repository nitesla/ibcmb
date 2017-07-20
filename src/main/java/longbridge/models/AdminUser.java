package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * AdminUser is a Staff of the bank tasked with administration of the item system 
 * and it configurations. They don't take part directly in transaction impacting activities
 */
@Entity
@Audited(withModifiedFlag=true)
@AuditOverride(forClass = User.class)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))

public class AdminUser extends User{

	private String authenticateMethod;

	public String getAuthenticateMethod() {
		return authenticateMethod;
	}

	public void setAuthenticateMethod(String authenticateMethod) {
		this.authenticateMethod = authenticateMethod;
	}
	
	public AdminUser(){

		this.userType = (UserType.ADMIN);

	}


	@Override
	public String toString() {
		return "AdminUser{" +super.toString()+"," +
				"authenticateMethod='" + authenticateMethod + '\'' +
				'}';
	}
}
