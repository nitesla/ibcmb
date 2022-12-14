package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Fortune on 6/8/2017.
 */
public class CorporateRoleDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;

    private int version;

    @NotEmpty(message = "name")
    private String name;

    @NotNull(message = "rank")
    Integer rank;

    String corporateId;

    boolean ruleMember;

    Set<CorporateUserDTO> users;


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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }


    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public Set<CorporateUserDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<CorporateUserDTO> users) {
        this.users = users;
    }

    public boolean isRuleMember() {
        return ruleMember;
    }

    public void setRuleMember(boolean ruleMember) {
        this.ruleMember = ruleMember;
    }

    @Override
    public String toString() {
        if(rank!=null) {
            return name + " " + rank;
        }
        else{
            return name;
        }
    }
}
