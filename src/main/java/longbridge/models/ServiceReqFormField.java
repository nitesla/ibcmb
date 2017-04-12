package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Entity
@Audited
public class ServiceReqFormField extends AbstractEntity {

    @ManyToOne
    private ServiceReqConfig serviceReqConfig;

    private String fieldName;
    private String fieldLabel;
    private String fieldType;
    private String typeData;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getTypeData() {
        return typeData;
    }

    public void setTypeData(String typeData) {
        this.typeData = typeData;
    }

    public ServiceReqConfig getServiceReqConfig() {
        return serviceReqConfig;
    }

    public void setServiceReqConfig(ServiceReqConfig serviceReqConfig) {
        this.serviceReqConfig = serviceReqConfig;
    }

    @Override
	public String toString() {
		return "ServiceReqFormFields [fieldName=" + fieldName + ", fieldLabel=" + fieldLabel + ", fieldType="
				+ fieldType + ", typeData=" + typeData + "]";
	}
    
    
}