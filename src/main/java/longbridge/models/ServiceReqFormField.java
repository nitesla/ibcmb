package longbridge.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codehaus.jackson.annotate.*;
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
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",scope = Long.class)
public class ServiceReqFormField extends AbstractEntity {

    @ManyToOne
    @org.codehaus.jackson.annotate.JsonBackReference
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
    @JsonIgnore
	public String toString() {
		return "ServiceReqFormFields [fieldName=" + fieldName + ", fieldLabel=" + fieldLabel + ", fieldType="
				+ fieldType + ", typeData=" + typeData + "]";
	}

    
    
}
