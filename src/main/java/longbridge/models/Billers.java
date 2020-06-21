package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;



@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(name = "BILLERS")
public class Billers extends AbstractEntity implements PrettySerializer{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "categoryid")
    private Long categoryId;
    @Column(name = "categoryname")
    private String categoryName;
    @Column(name = "categorydescription")
    private String categoryDescription;
    @Column(name = "billerid")
    private Long billerId;
    @Column(name = "billername")
    private String billerName;
    @Column(name = "customerfield1")
    private String customerField1;
    @Column(name = "customerfield2")
    private String customerField2;
    @Column(name = "currencySymbol")
    private String currencySymbol;
    @Column(name = "logoUrl")
    private String logoUrl;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name="deletevalue")
    private String deleteValue;

//    @OneToMany( mappedBy = "billers", cascade = CascadeType.ALL, orphanRemoval=true )
//    private List<PaymentItem> paymentItems;


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public Long getBillerId() {
        return billerId;
    }

    public void setBillerId(Long billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getCustomerField1() {
        return customerField1;
    }

    public void setCustomerField1(String customerField1) {
        this.customerField1 = customerField1;
    }

    public String getCustomerField2() {
        return customerField2;
    }

    public void setCustomerField2(String customerField2) {
        this.customerField2 = customerField2;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

//    public List<PaymentItem> getPaymentItems() {
//        return paymentItems;
//    }
//
//    public void setPaymentItems(List<PaymentItem> paymentItems) {
//        this.paymentItems = paymentItems;
//    }

    public String getDeleteValue() {
        return deleteValue;
    }

    public void setDeleteValue(String deleteValue) {
        this.deleteValue = deleteValue;
    }

    @Override
    public String toString() {
        return "Billers{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", billerId=" + billerId +
                ", billerName='" + billerName + '\'' +
                ", customerField1='" + customerField1 + '\'' +
                ", customerField2='" + customerField2 + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", enabled=" + enabled +
//                ", paymentItems=" + paymentItems +
                '}';
    }


    @Override @JsonIgnore
    public JsonSerializer<Billers> getSerializer() {
        return new JsonSerializer<Billers>() {
            @Override
            public void serialize(Billers value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
//				gen.writeStartObject();
//				gen.writeStringField("Name",value.name);
//				gen.writeStringField("Category",value.category);
//				gen.writeStringField("Owner Reference Name",value.ownerReferenceName);
//				gen.writeBooleanField("Enabled",value.enabled);
//				gen.writeEndObject();
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<Billers> getAuditSerializer() {
        return new JsonSerializer<Billers>() {
            @Override
            public void serialize(Billers value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
//				gen.writeStartObject();
//				if(value.id != null) {
//					gen.writeStringField("id", value.id.toString());
//				}else {
//					gen.writeStringField("id", "");
//				}
//				gen.writeStringField("name",value.name);
//				gen.writeStringField("category",value.category);
//				gen.writeStringField("ownerReferenceName",value.ownerReferenceName);
//				gen.writeBooleanField("enabled",value.enabled);
//				gen.writeEndObject();
            }
        };
    }

}
