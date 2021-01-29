package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.Verification;
import longbridge.utils.VerificationStatus;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Showboy on 01/08/2017.
 */
public class CorpUserVerificationDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String beforeObject; //json
    private String afterObject; //json
    private String original; //json

    private VerificationStatus status;

    private String description;
    private Long entityId;
    private String entityName;
    private String operation;

    private String initiatedBy;

    private Date initiatedOn;

    @NotEmpty(message = "comment is required")
    private String comments;

    private String verifiedBy;
    private Date verifiedOn;

    private Verification dependency;

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

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    @Override
    public String toString() {
        return "CorpUserVerificationDTO{" +
                "id=" + id +
                ", version=" + version +
                ", beforeObject='" + beforeObject + '\'' +
                ", afterObject='" + afterObject + '\'' +
                ", original='" + original + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", entityId=" + entityId +
                ", entityName='" + entityName + '\'' +
                ", operation='" + operation + '\'' +
                ", initiatedBy='" + initiatedBy + '\'' +
                ", initiatedOn=" + initiatedOn +
                ", comments='" + comments + '\'' +
                ", verifiedBy='" + verifiedBy + '\'' +
                ", verifiedOn=" + verifiedOn +
                ", dependency=" + dependency +
                '}';
    }
}
