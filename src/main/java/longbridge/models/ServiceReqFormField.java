package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class ServiceReqFormField extends AbstractEntity {

    @ManyToOne
    private SRConfig SRConfig;

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

    public SRConfig getSRConfig() {
        return SRConfig;
    }

    public void setSRConfig(SRConfig SRConfig) {
        this.SRConfig = SRConfig;
    }

    @Override
	public String toString() {
		return "ServiceReqFormFields [fieldName=" + fieldName + ", fieldLabel=" + fieldLabel + ", fieldType="
				+ fieldType + ", typeData=" + typeData + "]";
	}

	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}
