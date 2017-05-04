package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class UserGroup extends AbstractEntity {

    private String name;
    private String description;
    private Date dateCreated;

    @OneToMany(mappedBy = "userGroup")
    private List<OperationsUser> operationsUsers;

    @OneToMany(mappedBy = "userGroup")
    private List<PersonnelContact> personnelContacts;


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

    public List<OperationsUser> getOperationsUsers() {
        return operationsUsers;
    }

    public void setOperationsUser(List<OperationsUser> operationsUsers) {
        this.operationsUsers = operationsUsers;
    }

    public List<PersonnelContact> getPersonnelContacts() {
        return personnelContacts;
    }

    public void setPersonnelContacts(List<PersonnelContact> personnelContacts) {
        this.personnelContacts = personnelContacts;
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
