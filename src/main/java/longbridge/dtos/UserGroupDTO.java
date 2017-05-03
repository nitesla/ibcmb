package longbridge.dtos;

import longbridge.models.PersonnelContact;

import java.beans.PersistenceDelegate;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 5/3/2017.
 */
public class UserGroupDTO {

    private Long id;
    private int version;
    private String name;
    private String description;
    private Date dateCreated;

    private List<OperationsUserDTO> operationsUserList;

    private List<PersonnelContactDTO> personnelContactList;


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

    public List<OperationsUserDTO> getOperationsUserList() {
        return operationsUserList;
    }

    public void setOperationsUserList(List<OperationsUserDTO> operationsUserList) {
        this.operationsUserList = operationsUserList;
    }

    public List<PersonnelContactDTO> getPersonnelContactList() {
        return personnelContactList;
    }

    public void setPersonnelContactList(List<PersonnelContactDTO> personnelContactList) {
        this.personnelContactList = personnelContactList;
    }
}
