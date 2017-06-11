package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class UserGroup extends AbstractEntity {

    private String name;
    private String description;
    private Date dateCreated;


    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private List<OperationsUser> users;

    @JsonProperty
    @OneToMany(cascade = CascadeType.ALL)
    private List<Contact> contacts;


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



    public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}


	public List<OperationsUser> getUsers() {
		return users;
	}

	public void setUsers(List<OperationsUser> users) {
		this.users = users;
	}

	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "user_userGroup", joinColumns =
//    @JoinColumn(name = "user_id", referencedColumnName = "Id"), inverseJoinColumns =
//    @JoinColumn(name = "group_id", referencedColumnName = "Id"))
//    private Collection<User> users;

}
