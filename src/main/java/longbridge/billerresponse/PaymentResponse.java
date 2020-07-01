
package longbridge.billerresponse;

import longbridge.dtos.BillPaymentDTO;

import java.util.List;

public class PaymentResponse {

    private List<BillPaymentDTO> billPayment = null;

    public List<BillPaymentDTO> billPayment() {
        return billPayment;
    }

    public void setBillPayment(List<BillPaymentDTO> billPayment) {
        this.billPayment = billPayment;
    }
}
