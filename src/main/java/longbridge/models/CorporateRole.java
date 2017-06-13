package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Fortune on 6/7/2017.
 */

@Entity
@Audited(withModifiedFlag = true)
@Where(clause = "del_Flag='N'")
public class CorporateRole extends AbstractEntity {

    String name;
    Integer rank;
    String roleType;

    @ManyToOne
    Corporate corporate;

    @OneToMany
    Set<CorporateUser> users = new HashSet<CorporateUser>();

    @OneToMany
    List<PendAuth> pendAuths;

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

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public Set<CorporateUser> getUsers() {
        return users;
    }

    public void setUsers(Set<CorporateUser> users) {
        this.users = users;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public List<PendAuth> getPendAuths() {
        return pendAuths;
    }

    public void setPendAuths(List<PendAuth> pendAuths) {
        this.pendAuths = pendAuths;
    }
}
