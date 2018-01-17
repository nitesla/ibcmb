package longbridge.models;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Longbridge on 7/5/2017.
 */
@Entity
public class CronJobJar extends AbstractEntity {
    private String jobDetails;
    private Date addeOn;
    private String addedBy;
    private String jarName;

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    public Date getAddeOn() {
        return addeOn;
    }

    public void setAddeOn(Date addeOn) {
        this.addeOn = addeOn;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
}
