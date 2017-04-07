package longbridge.models;

import javax.persistence.*;
import java.util.Date;

/** this represents a change that needs Verification. This can be
 * used to store an update if a verification is made. It also serves as an audit for every change
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Entity
public class Verification extends  AbstractEntity {

    @Column(columnDefinition = "TEXT")
    private String beforeObject; //json
    @Column(columnDefinition = "TEXT")
    private String afterObject; //json
    @Column(columnDefinition = "TEXT")
    private String original; //json

    private String description;

    private Long verifiedId;
    @Enumerated(value = EnumType.STRING)
    private OperationCode operationCode;

    @ManyToOne
    private AdminUser initiatedBy;
    private Date initiatedOn;

    @ManyToOne
    private AdminUser declinedBy;
    private Date declinedOn;
    private String declineReason;

    @ManyToOne
    private AdminUser verifiedBy;
    private Date verifiedOn;

    public AdminUser getDeclinedBy() {
        return declinedBy;
    }

    public void setDeclinedBy(AdminUser declinedBy) {
        this.declinedBy = declinedBy;
    }


    public String getDeclineReason() {
        return declineReason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getBeforeObject() {
        return beforeObject;
    }

    public void setBeforeObject(String beforeObject) {
        this.beforeObject = beforeObject;
    }

    public String getAfterObject() {
        return afterObject;
    }

    public void setAfterObject(String afterObject) {
        this.afterObject = afterObject;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public Long getVerifiedId() {
        return verifiedId;
    }

    public void setVerifiedId(Long verifiedId) {
        this.verifiedId = verifiedId;
    }

    public OperationCode getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(OperationCode operationCode) {
        this.operationCode = operationCode;
    }

    public AdminUser getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(AdminUser initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Date getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(Date initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public Date getDeclinedOn() {
        return declinedOn;
    }

    public void setDeclinedOn(Date declinedOn) {
        this.declinedOn = declinedOn;
    }

    public Date getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(Date verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public AdminUser getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(AdminUser verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

}
