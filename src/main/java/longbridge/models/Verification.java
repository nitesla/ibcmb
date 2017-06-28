package longbridge.models;


import longbridge.utils.verificationStatus;
import org.hibernate.annotations.Where;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * this represents a change that needs Verification. This can be
 * used to store an update if a verification is made. It also serves as an audit for every change
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Entity
@Audited(withModifiedFlag = true)
@Where(clause = "del_Flag='N'")
public class Verification extends AbstractEntity {


    @Lob
    private String beforeObject; //json
    @Lob
    private String afterObject; //json
    @Lob
    private String originalObject; //json
    @Enumerated(value = EnumType.STRING)
    private verificationStatus status;
    private String description;
    private Long entityId;
    private String entityName;
    @Enumerated(value = EnumType.STRING)
    private UserType userType;
    private String operation;
    private String comment;
    private String initiatedBy;
    private Date initiatedOn;
    private String declinedBy;
    private Date declinedOn;
    private String declineReason;
    private String verifiedBy;
    private Date verifiedOn;
    @OneToOne
    private Verification dependency;

    public Verification getDependency() {
        return dependency;
    }

    public void setDependency(Verification dependency) {
        this.dependency = dependency;
    }


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public verificationStatus getStatus() {
        return status;
    }

    public void setStatus(verificationStatus status) {
        this.status = status;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getOriginalObject() {
        return originalObject;
    }

    public void setOriginalObject(String originalObject) {
        this.originalObject = originalObject;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDeclinedBy() {
        return declinedBy;
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


    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public void setDeclinedBy(String declinedBy) {
        this.declinedBy = declinedBy;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public static OperationCode getAddCode() {
        // TODO Auto-generated method stub
        return null;
    }

    public static OperationCode getModifyCode() {
        // TODO Auto-generated method stub
        return null;
    }

}
