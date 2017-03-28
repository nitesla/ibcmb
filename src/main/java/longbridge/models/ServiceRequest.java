package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Long senderId;
    private String serviceRequestType;
    private String subject;
    private String body;
    private Long recepientId;
    private Long groupRecepientId;
    private Date date;

}
