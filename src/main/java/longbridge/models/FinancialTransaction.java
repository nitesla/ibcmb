package longbridge.models;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by Wunmi on 3/28/2017.
 */

@Entity
public class FinancialTransaction extends AbstractEntity{

    private Long transactionType;
    private Long amount;
    private String remarks;
    private Date dateTime;
    @ManyToOne
    private Account account;

}
