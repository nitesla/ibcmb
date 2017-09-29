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

    @Override
    public String toString() {
        return "AuditDTO{" +
                "entityDetails=" + entityDetails +
                ", modifiedEntities=" + modifiedEntities +
                '}';
    }
}
