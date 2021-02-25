package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 5/3/2017.
 */
public class UserGroupDTO implements Serializable {

    private Long id;
    private int version;
    @NotEmpty(message = "name")
    private String name;
    private String description;
    private Date dateCreated;

    private List<OperationsUserDTO> users;

    private List<ContactDTO> contacts;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

	public List<OperationsUserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<OperationsUserDTO> users) {
		this.users = users;
	}

	public List<ContactDTO> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactDTO> contacts) {
		this.contacts = contacts;
	}

}
