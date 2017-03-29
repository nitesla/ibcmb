package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class ServiceRequest extends AbstractEntity{
<<<<<<< HEAD
=======

>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae

    private Long senderId;
    private String serviceRequestType;
    private String subject;
    private String body;
    private Long recepientId;
    private Long groupRecepientId;
    private Date date;

}
