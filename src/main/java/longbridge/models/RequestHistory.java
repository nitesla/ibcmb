package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class RequestHistory extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Long requestId;
    private String status;
    private String comment;
    private Long updatedBy;
    private Date date;
}
