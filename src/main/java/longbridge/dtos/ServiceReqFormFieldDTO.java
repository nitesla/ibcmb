package longbridge.dtos;

import longbridge.models.AbstractEntity;
import longbridge.models.ServiceReqConfig;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Wunmi on 08/04/2017.
 */

public class ServiceReqFormFieldDTO{


    private String id;
    private int version;
    private String fieldName;
    private String fieldLabel;
    private String fieldType;
    private String typeData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

	@Override
	public String toString() {
		return "ServiceReqFormFields [fieldName=" + fieldName + ", fieldLabel=" + fieldLabel + ", fieldType="
				+ fieldType + ", typeData=" + typeData + "]";
	}
    
    
}
