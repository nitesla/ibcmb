package longbridge.dtos;

 import java.io.Serializable;

/**
 * Created by mac on 22/02/2018.
 */
public class PaymentStatDTO implements Serializable {

    private Long id;

    private String amount;
    private String category;
    private String date;
    private String paymentItem;
    private String customerAccountNumber;

    private String status ;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(String paymentItem) {
        this.paymentItem = paymentItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
