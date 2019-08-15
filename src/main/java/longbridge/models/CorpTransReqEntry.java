package longbridge.models;

import longbridge.utils.TransferAuthorizationStatus;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Fortune on 7/5/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpTransReqEntry extends AbstractEntity {

    private Long tranReqId;
    private String comments;

    private String status;

    @Enumerated(EnumType.ORDINAL)
    private TransferAuthorizationStatus authStatus;

    @ManyToOne
    private CorporateRole role;

    @ManyToOne
    private CorporateUser user;
    private Date entryDate;
    @Transient
    private String channel;
    @Transient
    private String tranLocation;


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TransferAuthorizationStatus getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(TransferAuthorizationStatus authStatus) {
        this.authStatus = authStatus;
    }

    public CorporateRole getRole() {
        return role;
    }

    public void setRole(CorporateRole role) {
        this.role = role;
    }

    public CorporateUser getUser() {
        return user;
    }

    public void setUser(CorporateUser user) {
        this.user = user;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Long getTranReqId() {
        return tranReqId;
    }

    public void setTranReqId(Long tranReqId) {
        this.tranReqId = tranReqId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTranLocation() {
        return tranLocation;
    }

    public void setTranLocation(String tranLocation) {
        this.tranLocation = tranLocation;
    }

    @Override
    public String   toString() {
        return "CorpTransReqEntry{" +
                "tranReqId=" + tranReqId +
                ", comments='" + comments + '\'' +
                ", status='" + status + '\'' +
                ", authStatus=" + authStatus +
                ", role=" + role +
                ", user=" + user +
                ", entryDate=" + entryDate +
                ", channel='" + channel + '\'' +
                ", tranLocation='" + tranLocation + '\'' +
                '}';
    }
}
