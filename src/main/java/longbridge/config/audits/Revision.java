package longbridge.config.audits;

import com.fasterxml.jackson.annotation.JsonBackReference;
import longbridge.config.audits.listeners.EntityRevisionListener;
import longbridge.models.UserType;
import org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@RevisionEntity(EntityRevisionListener.class)
public class Revision extends DefaultTrackingModifiedEntitiesRevisionEntity {


    private String ip;
    private String trace;

    private String lastChangedBy;
    private String approvedBy;
    private String ipAddress;

    private UserType type;

    @JsonBackReference
    @OneToMany(mappedBy = "revision", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<ModifiedType> modifiedTypes = new HashSet<>();

    public void addModifiedEntityType(String entityClassName) {
        modifiedTypes.add(new ModifiedType(this, entityClassName));
    }

    public Set<ModifiedType> getModifiedTypes() {
        return modifiedTypes;
    }

    public String getLastChangedBy() {
        return lastChangedBy;
    }

    public void setLastChangedBy(String lastChangedBy) {
        this.lastChangedBy = lastChangedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }
}
