package longbridge.models;


import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class EntityId {
    private Long eid;
    private UserType type;

    public Long getEid() {
        return eid;
    }

    public void setEid(Long eid) {
        this.eid = eid;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityId entityId = (EntityId) o;
        return eid.equals(entityId.eid) &&
                type == entityId.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eid, type);
    }
}
