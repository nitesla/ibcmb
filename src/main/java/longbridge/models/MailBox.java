package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited
public class MailBox extends AbstractEntity{
    private Long userId;
    private UserType userType;

    @OneToMany(mappedBy = "mailBox")
    private Collection<Message> messages;
}
