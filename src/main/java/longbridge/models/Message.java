package longbridge.models;

import javax.persistence.*;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Message extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Long userId;
    private String recepient;
    private String subject;
    private String body;
    private String dateTime;
    private String status;
    private String location;

}
