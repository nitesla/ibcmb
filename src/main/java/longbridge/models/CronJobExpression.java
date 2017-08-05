package longbridge.models;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Longbridge on 6/15/2017.
 */
@Entity
public class CronJobExpression extends AbstractEntity {
    private String cronExpression;
    private Date createdOn;
    private String username;
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "CronJobExpression{" +
                "cronExpression='" + cronExpression + '\'' +
                ", createdOn=" + createdOn +
                ", username=" + username +
                '}';
    }
}