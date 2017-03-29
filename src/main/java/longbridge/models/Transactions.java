package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Transactions extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Long transactionType;
    private Long customerId;
    private Long beneficiaryId;
    private Long amount;
    private String remarks;
    private Date dateTime;

}
