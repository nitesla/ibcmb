package longbridge.servicerequests.client;

import longbridge.models.OperationsUser;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public class Comment implements Serializable {

    private String status;
    @Lob
    private String comments;
    @ManyToOne
    private OperationsUser createdBy;
    private Date createdOn;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public OperationsUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(OperationsUser createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (status != null ? !status.equals(comment.status) : comment.status != null) return false;
        if (comments != null ? !comments.equals(comment.comments) : comment.comments != null) return false;
        if (!createdBy.equals(comment.createdBy)) return false;
        return createdOn.equals(comment.createdOn);
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + createdOn.hashCode();
        return result;
    }
}
