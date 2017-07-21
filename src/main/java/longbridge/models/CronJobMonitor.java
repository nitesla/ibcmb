package longbridge.models;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Longbridge on 7/5/2017.
 */
@Entity
public class CronJobMonitor extends AbstractEntity {
    private Date jobStartTime;
    private Date jobEndTime;
    private String expression;
    private String jobCategory;
    private boolean stillRunning;

    public boolean getStillRunning() {
        return stillRunning;
    }

    public void setStillRunning(boolean stillRunning) {
        this.stillRunning = stillRunning;
    }

    public Date getJobStartTime() {

        return jobStartTime;
    }

    public void setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public Date getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(Date jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }
    @Override
    public String toString() {
        return "CronJobMonitor{" +
                "jobStartTime=" + jobStartTime +
                ", jobEndTime=" + jobEndTime +
                ", expression='" + expression + '\'' +
                ", jobCategory='" + jobCategory + '\'' +
                '}';
    }


}
