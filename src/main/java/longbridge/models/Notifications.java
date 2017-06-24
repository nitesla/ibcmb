package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Created by Showboy on 24/06/2017.
 */
@Entity
@Where(clause ="del_Flag='N'")
public class Notifications extends AbstractEntity {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "message='" + message + '\'' +
                '}';
    }
}
