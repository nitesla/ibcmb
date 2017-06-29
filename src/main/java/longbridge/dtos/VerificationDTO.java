package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.AdminUser;
import longbridge.models.OperationCode;
import longbridge.models.Verification;
import longbridge.utils.verificationStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * Created by chiomarose on 19/06/2017.
 */
public class VerificationDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String beforeObject; //json
    private String afterObject; //json
    private String original; //json

    private verificationStatus status;

    private String description;
    private Long entityId;
    private String entityName;
    private String operation;

    private String initiatedBy;

    private Date initiatedOn;

    private String declinedBy;
    private Date declinedOn;
    private String declineReason;


    private String verifiedBy;
    private Date verifiedOn;

    private Verification dependency;

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

    public verificationStatus getStatus() {
        return status;
    }

    public void setStatus(verificationStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

     public Date getDeclinedOn() {
        return declinedOn;
    }

    public void setDeclinedOn(Date declinedOn) {
        this.declinedOn = declinedOn;
    }


    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Date getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(Date initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public String getDeclinedBy() {
        return declinedBy;
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

    public Date getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(Date verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public Verification getDependency() {
        return dependency;
    }

    public void setDependency(Verification dependency) {
        this.dependency = dependency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
