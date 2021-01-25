package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Biller extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private Long billerId;
    private String billerName;
    private String customerField1;
    private String customerField2;
    private String currencySymbol;
    private String logoUrl;
    private boolean enabled;
    private String supportemail;
    private String shortname;
    private String paydirectProductId;
    private String paydirectInstitutionId;
    private Long surcharge;
    private Long currencyCode;
    private String narration;


//    @OneToMany( mappedBy = "billers", cascade = CascadeType.ALL, orphanRemoval=true )
//    private List<PaymentItem> paymentItems;




    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getSupportemail() {
        return supportemail;
    }

    public void setSupportemail(String supportemail) {
        this.supportemail = supportemail;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getPaydirectProductId() {
        return paydirectProductId;
    }

    public void setPaydirectProductId(String paydirectProductId) {
        this.paydirectProductId = paydirectProductId;
    }

    public String getPaydirectInstitutionId() {
        return paydirectInstitutionId;
    }

    public void setPaydirectInstitutionId(String paydirectInstitutionId) {
        this.paydirectInstitutionId = paydirectInstitutionId;
    }

    public Long getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(Long surcharge) {
        this.surcharge = surcharge;
    }

    public Long getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Long currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    //    public List<PaymentItem> getPaymentItems() {
//        return paymentItems;
//    }
//
//    public void setPaymentItems(List<PaymentItem> paymentItems) {
//        this.paymentItems = paymentItems;
//    }


    @Override
    public String toString() {
        return "Biller{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", billerId=" + billerId +
                ", billerName='" + billerName + '\'' +
                ", customerField1='" + customerField1 + '\'' +
                ", customerField2='" + customerField2 + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", enabled=" + enabled +
                ", supportemail='" + supportemail + '\'' +
                ", shortname='" + shortname + '\'' +
                ", paydirectProductId='" + paydirectProductId + '\'' +
                ", paydirectInstitutionId='" + paydirectInstitutionId + '\'' +
                ", surcharge=" + surcharge +
                ", currencyCode=" + currencyCode +
                ", narration='" + narration + '\'' +
                '}';
    }
}
