package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

public class ContactDTO implements Serializable {

	private long dt_RowId;
	private String firstName;
	private String lastName;
	private String email;
	private boolean external;
	
	public ContactDTO() {
	}
	public ContactDTO(String firstName, String lastName, String email, boolean external) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.external = external;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}
	public boolean isExternal() {
		return external;
	}
	
	
	public long getDt_RowId() {
		return dt_RowId;
	}
	
	@JsonSetter("DT_RowId")
	public void setDt_RowId(long dt_RowId) {
		this.dt_RowId = dt_RowId;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setExternal(boolean external) {
		this.external = external;
	}
	
	
}
