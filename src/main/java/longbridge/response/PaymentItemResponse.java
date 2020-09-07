package longbridge.response;

import longbridge.dtos.PaymentItemDTO;

import java.util.List;

public class PaymentItemResponse {

    private List<PaymentItemDTO> paymentitems = null;

    public List<PaymentItemDTO> getPaymentitems() {
        return paymentitems;
    }

    public void setPaymentitems(List<PaymentItemDTO> paymentitems) {
        this.paymentitems = paymentitems;
    }


}
