package longbridge.dtos;

import longbridge.models.EntityId;

import java.io.Serializable;

public class AddCoverageDTO implements Serializable {

    private EntityId id;
    private String code;

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
