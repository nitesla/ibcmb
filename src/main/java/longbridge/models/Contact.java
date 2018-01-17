package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@Audited(withModifiedFlag=true)
public class Contact extends AbstractEntity implements Person {

	public Contact(){
	}
	

	public Contact(String firstName, String lastName, String email, boolean external) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.external = external;
	}

	@ManyToOne
	UserGroup userGroup;



	@Override
	public String getFirstName() {
		return firstName;
	}
	
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Override
	public String getLastName() {
		return lastName;
	}
	

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	

	@Override
	public String getEmail() {
		return email;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}
	String firstName;
	String lastName;
	String email;
	boolean external;
	@Override
	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}


	@Override
	public String toString() {
		return "Contact [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", external="
				+ external + "]";
	}
	
	
}
