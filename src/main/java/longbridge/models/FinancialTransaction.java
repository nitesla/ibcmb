package longbridge.models;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Fortune on 3/28/2017.
 */


public class FinancialTransaction extends AbstractEntity{

    private Long transactionType;
    private Long customerId;
    private Long beneficiaryId;
    private Long amount;
    private String remarks;
    private Date dateTime;
}
