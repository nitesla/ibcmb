package longbridge.dtos;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class CorpTransRoleDTO implements Serializable {

	private Long id;
	private int version;

	@NotEmpty(message = "numOfRole")
	private int numOfRole = 1;

	private CorporateRoleDTO role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CorporateRoleDTO getRole() {
		return role;
	}

	public void setRole(CorporateRoleDTO role) {
		this.role = role;
	}

	public int getNumOfRole() {
		return numOfRole;
	}

	public void setNumOfRole(int numOfRole) {
		this.numOfRole = numOfRole;
	}

}
