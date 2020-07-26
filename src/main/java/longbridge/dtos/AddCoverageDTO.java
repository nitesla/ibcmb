package longbridge.dtos;

import longbridge.models.EntityId;

public class AddCoverageDTO {

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
