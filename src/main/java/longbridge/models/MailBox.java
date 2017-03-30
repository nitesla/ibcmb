package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class MailBox extends AbstractEntity{


    private Long user_Id;

    @Override
    public String toString() {
        return "MailBox{" +
                "user_Id=" + user_Id +
                '}';
    }

    public Long getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(Long user_Id) {
        this.user_Id = user_Id;
    }
}
