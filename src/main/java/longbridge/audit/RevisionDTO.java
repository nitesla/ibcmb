package longbridge.audit;

import java.util.Date;
import java.util.List;

public class RevisionDTO {

    private Date from;

    private Date to;

    private Long tableIndex;

    private Long revisionId;

    private String modifier;

    private String approver;

    private List<String> operations;

    private String ipAddress;
    private String entityName;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Long getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(Long tableIndex) {
        this.tableIndex = tableIndex;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }
}
