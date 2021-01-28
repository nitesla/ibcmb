package longbridge.dtos;

import longbridge.models.EntityId;

import java.io.Serializable;

public class UpdateCoverageDTO implements Serializable {

    private EntityId id;
    private String code;
    private Boolean enabled;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public EntityId getId() {
        return id;
    }

    public void setId(EntityId id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
