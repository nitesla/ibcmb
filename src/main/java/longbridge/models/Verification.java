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
    private String original; //json

    @Enumerated(value = EnumType.STRING)
    private verificationStatus status;

    private String description;
    private Long entityId;
    private String entityName;
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

    @OneToOne
    private Verification dependency;

    public Verification getDependency() {
        return dependency;
    }

    public void setDependency(Verification dependency) {
        this.dependency = dependency;
    }

    public AdminUser getDeclinedBy() {
        return declinedBy;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setDeclinedBy(AdminUser declinedBy) {
        this.declinedBy = declinedBy;
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

    @Override
    public String toString() {
        return "Verification [" + (beforeObject != null ? "beforeObject=" + beforeObject + ", " : "")
                + (afterObject != null ? "afterObject=" + afterObject + ", " : "")
                + (original != null ? "original=" + original + ", " : "")
                + (status != null ? "status=" + status + ", " : "")
                + (description != null ? "description=" + description + ", " : "")
                + (entityId != null ? "entityId=" + entityId + ", " : "")
                + (operationCode != null ? "operationCode=" + operationCode + ", " : "")
                + (initiatedBy != null ? "initiatedBy=" + initiatedBy + ", " : "")
                + (initiatedOn != null ? "initiatedOn=" + initiatedOn + ", " : "")
                + (declinedBy != null ? "declinedBy=" + declinedBy + ", " : "")
                + (declinedOn != null ? "declinedOn=" + declinedOn + ", " : "")
                + (declineReason != null ? "declineReason=" + declineReason + ", " : "")
                + (verifiedBy != null ? "verifiedBy=" + verifiedBy + ", " : "")
                + (verifiedOn != null ? "verifiedOn=" + verifiedOn : "") + "]";
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
