package longbridge.models;

import javax.persistence.Entity;

/**
 * AdminUser is a Staff of the bank tasked with administration of the item system 
 * and it configurations. They don't take part directly in transaction impacting activities
 */
@Entity
public class AdminUser extends User {

	private String authenticateMethod;

	public String getAuthenticateMethod() {
		return authenticateMethod;
	}

	public void setAuthenticateMethod(String authenticateMethod) {
		this.authenticateMethod = authenticateMethod;
	}
	
	public AdminUser(){
		this.usertype = (UserType.ADMIN.toString());
	}

	public String toString2() {
		return "AdminUser ["+super.toString()+"]";
	}

	@Override
	public String toString() {
		return "AdminUser{" +super.toString()+"," +
				"authenticateMethod='" + authenticateMethod + '\'' +
				'}';
	}
}
