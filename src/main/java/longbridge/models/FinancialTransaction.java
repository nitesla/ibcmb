package longbridge.models;

import java.util.Date;
/**
 * Created by Wunmi on 3/28/2017.
 */


public class FinancialTransaction extends AbstractEntity{

    private Long transactionType;
    private Long customerId;
    private Long beneficiaryId;
    private Long amount;
    private String remarks;
    private Date dateTime;

}
