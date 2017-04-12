package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class MailBox extends AbstractEntity{
    private Long userId;
    private UserType userType;

    @OneToMany(mappedBy = "mailBox")
    private Collection<Message> messages;
}
