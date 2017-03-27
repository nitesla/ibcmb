package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Investment {

    @Id
    private Long Id;
    private String referenceNumber;
    private Date bookingDate;
    private Date valueDate;
    private Date maturityDate;

}
