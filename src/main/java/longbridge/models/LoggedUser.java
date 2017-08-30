package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Created by ayoade_farooq@yahoo.com on 8/30/2017.
 */
@Entity
@Where(clause ="del_Flag='N'" )
public class LoggedUser extends AbstractEntity {

    private String username;
    private String userType;
    private String sessionId;


    public LoggedUser() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "LoggedUser{" +
                "username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
