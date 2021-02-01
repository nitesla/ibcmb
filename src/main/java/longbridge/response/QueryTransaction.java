
package longbridge.response;

import longbridge.dtos.BillPaymentDTO;

import java.util.List;

public class QueryTransaction {

    private List<BillPaymentDTO> queryTransaction = null;

    public List<BillPaymentDTO> getQueryTransaction() {
        return queryTransaction;
    }

    public void setQueryTransaction(List<BillPaymentDTO> queryTransaction) {
        this.queryTransaction = queryTransaction;
    }
}
