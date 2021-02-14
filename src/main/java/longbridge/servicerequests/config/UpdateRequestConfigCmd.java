package longbridge.servicerequests.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateRequestConfigCmd implements RequestConfigCmd, Serializable {
    private Long id;
    private String name;
    private String description;
    private String type;
    private boolean authRequired;
    private Long groupId;
    private List<SRFieldDTO> fields = new ArrayList<>();;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SRFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<SRFieldDTO> fields) {
        this.fields = fields;
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

    public String getType() {
        return type;
    }


    public boolean isAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
