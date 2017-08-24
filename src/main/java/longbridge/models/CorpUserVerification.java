package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import longbridge.utils.VerificationStatus;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Showboy on 28/07/2017.
 */
@Entity
@Where(clause = "del_Flag='N'")
public class CorpUserVerification extends AbstractEntity{
    @Lob
    private String beforeObject; //json
    @Lob
    private String afterObject; //json
    @Lob
    private String originalObject; //json
    @Enumerated(value = EnumType.STRING)
    private VerificationStatus status;
    private String description;
    private Long entityId;
    private String entityName;
    private Long corpId;
    @Enumerated(value = EnumType.STRING)
    private CorpUserType corpUserType;
    private String operation;
    private String comments;
    private String initiatedBy;
    //    @Temporal(TemporalType.DATE)
    private Date initiatedOn;
    private String verifiedBy;
    //    @Temporal(TemporalType.DATE)
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

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Date getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(Date initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public Date getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(Date verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public CorpUserType getCorpUserType() {
        return corpUserType;
    }

    public void setCorpUserType(CorpUserType corpUserType) {
        this.corpUserType = corpUserType;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Long getCorpId() {
        return corpId;
    }

    public void setCorpId(Long corpId) {
        this.corpId = corpId;
    }

    @Override @JsonIgnore
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("entityName", "initiatedBy","verifiedBy");
    }

}