package longbridge.dtos;

import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.models.AbstractEntity;
import longbridge.models.Code;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * Created by Longbridge on 9/27/2017.
 */
public class AuditDTO {
    Object entityDetails;
    ModifiedEntityTypeEntity modifiedEntities;
    String entityClassName;
String finacialInstitution;
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

    public String getFinacialInstitution() {
        return finacialInstitution;
    }

    public void setFinacialInstitution(String finacialInstitution) {
        this.finacialInstitution = finacialInstitution;
    }

    @Override
    public String toString() {
        return "AuditDTO{" +
                "entityDetails=" + entityDetails +
                ", modifiedEntities=" + modifiedEntities +
                ", entityClassName='" + entityClassName + '\'' +
                ", finacialInstitution='" + finacialInstitution + '\'' +
                '}';
    }
}
