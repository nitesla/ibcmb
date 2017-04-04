package longbridge.models;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class MailBox extends AbstractEntity{
    private Long userId;
    private UserType userType;
    private Collection<Message> messages;
}
