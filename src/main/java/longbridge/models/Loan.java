package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Loan extends AbstractEntity{

    private String loanType;
    private LocalDateTime startDate;
    private Long amount;
    private String tenure;
    private Long rate;

}
