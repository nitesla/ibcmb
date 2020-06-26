package longbridge.dtos;

public class PaymentItemDTO {


    private String categoryid;
    private Long billerid;
    private Boolean isAmountFixed;
    private Long paymentitemid;
    private String paymentitemname;
    private Double amount;
    private Long code;
    private Long currencyCode;
    private String currencySymbol;
    private String itemCurrencySymbol;
    private String sortOrder;
    private String pictureId;
    private Long paymentCode;
    private Boolean enabled;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public Long getBillerid() {
        return billerid;
    }

    public void setBillerid(Long billerid) {
        this.billerid = billerid;
    }

    public Boolean getIsAmountFixed() {
        return isAmountFixed;
    }

    public void setIsAmountFixed(Boolean isAmountFixed) {
        this.isAmountFixed = isAmountFixed;
    }

    public Long getPaymentitemid() {
        return paymentitemid;
    }

    public void setPaymentitemid(Long paymentitemid) {
        this.paymentitemid = paymentitemid;
    }

    public String getPaymentitemname() {
        return paymentitemname;
    }

    public void setPaymentitemname(String paymentitemname) {
        this.paymentitemname = paymentitemname;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Long getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Long currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getItemCurrencySymbol() {
        return itemCurrencySymbol;
    }

    public void setItemCurrencySymbol(String itemCurrencySymbol) {
        this.itemCurrencySymbol = itemCurrencySymbol;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public Long getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(Long paymentCode) {
        this.paymentCode = paymentCode;
    }

    public Boolean getAmountFixed() {
        return isAmountFixed;
    }

    public void setAmountFixed(Boolean amountFixed) {
        isAmountFixed = amountFixed;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
