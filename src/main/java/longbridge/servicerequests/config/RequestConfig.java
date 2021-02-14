package longbridge.servicerequests.config;


import longbridge.models.AbstractEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Entity
public class RequestConfig extends AbstractEntity implements Serializable {

    @ElementCollection(fetch = FetchType.EAGER)
    List<RequestField> fields;
    private String name;
    private String type;
    private boolean authRequired;
    private Long groupId;
    private String description;
    private boolean system;


    public RequestConfig() {
        fields = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(RequestField field) {
        fields.add(field);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public List<RequestField> getFields() {
        return fields;
    }

    public void setFields(List<RequestField> fields) {
        this.fields = fields;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("name", "type");
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
