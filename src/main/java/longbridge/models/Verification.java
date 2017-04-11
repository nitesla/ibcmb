package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/** this represents a change that needs Verification. This can be
 * used to store an update if a verification is made. It also serves as an audit for every change
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Entity
@Audited
public class Verification extends  AbstractEntity {
    public static enum VerificationStatus {
        VERIFIED, DECLINED,PENDING //EXPIRED
    }

    @Column(columnDefinition = "TEXT")
    private String beforeObject; //json
    @Column(columnDefinition = "TEXT")
    private String afterObject; //json
    @Column(columnDefinition = "TEXT")
    private String original; //json

    @Enumerated(value = EnumType.STRING)
    private VerificationStatus status;

    private String description;
    private Long entityId;
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

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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
