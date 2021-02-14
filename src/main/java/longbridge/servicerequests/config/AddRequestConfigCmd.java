package longbridge.servicerequests.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddRequestConfigCmd implements RequestConfigCmd , Serializable {
    List<SRFieldDTO> fields = new ArrayList<>();
    @NotEmpty(message = "name")
    private String name;
    private String type;
    private boolean authRequired;
    @NotNull(message = "groupId")
    private Long groupId;
    private String description;

    @Override
    public List<SRFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<SRFieldDTO> fields) {
        this.fields = fields;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
