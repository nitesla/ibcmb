package longbridge.dtos;

import longbridge.config.audits.ModifiedEntityTypeEntity;
import org.json.simple.JSONObject;

/**
 * Created by Longbridge on 9/27/2017.
 */
public class AuditDTO {
    Object entityDetails;
    ModifiedEntityTypeEntity modifiedEntities;
    String entityClassName;
    JSONObject fullEntity;

    public Object getEntityDetails() {
        return entityDetails;
    }

    public void setEntityDetails(Object entityDetails) {
        this.entityDetails = entityDetails;
    }

    public ModifiedEntityTypeEntity getModifiedEntities() {
        return modifiedEntities;
    }

    public void setModifiedEntities(ModifiedEntityTypeEntity modifiedEntities) {
        this.modifiedEntities = modifiedEntities;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public JSONObject getFullEntity() {
        return fullEntity;
    }

    public void setFullEntity(JSONObject fullEntity) {
        this.fullEntity = fullEntity;
    }

    @Override
    public String toString() {
        return "AuditDTO{" +
                "entityDetails=" + entityDetails +
                ", modifiedEntities=" + modifiedEntities +
                ", entityClassName='" + entityClassName + '\'' +
                ", fullEntity=" + fullEntity +
                '}';
    }
}
