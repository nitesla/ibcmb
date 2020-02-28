package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import javax.persistence.Entity;
import java.util.Date;


public class CustomerFeedBackDTO{

    private String transactionType;
    private String comments;
    private Integer ratings;
    private Date createdOn=new Date();
    private String userType;
    private String transactionRef;


    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getRatings() {
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

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
    @Override
    public String toString() {
        return "CustomerFeedBackDTO{" +
                ", transactionType='" + transactionType + '\'' +
                ", comments='" + comments + '\'' +
                ", ratings=" + ratings +
                ", createdOn=" + createdOn +
                ", userType='" + userType + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                '}';
    }




}
