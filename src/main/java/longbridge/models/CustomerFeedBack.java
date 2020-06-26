package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_flag='N'")
public class CustomerFeedBack extends AbstractEntity {
    private String transactionRef;
    private String comments;

    private Integer ratings;
    private Date createdOn= new Date();
    private String userType;

    private String transactionType;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getRatings() {
        return ratings;
    }

    public void setRatings(Integer ratings) {
        this.ratings = ratings;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "CustomerFeedBack{" +
                "transactionRef='" + transactionRef + '\'' +
                ", comments='" + comments + '\'' +
                ", ratings=" + ratings +
                ", createdOn=" + createdOn +
                ", userType='" + userType + '\'' +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }



}