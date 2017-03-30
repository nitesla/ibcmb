package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Loan extends AbstractEntity{


    private String loanType;
    private Date startDate;
    private Long amount;
    private String tenure;
    private Long rate;

}
