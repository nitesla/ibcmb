package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.Permission;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 30/01/2018.
 */
public class ReportDTO {
//    @JsonProperty("DT_RowId")
private Long id;
    private int version;
    private String reportName;
    private Date createdOn;
    private String createdBy;
    private String origFileName;
    private String sysFileName;
    private Permission permission;
    private List<ReportParameterDTO> reportParameters;
    private byte[] jrxmlFile;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<ReportParameterDTO> getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(List<ReportParameterDTO> reportParameters) {
        this.reportParameters = reportParameters;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getOrigFileName() {
        return origFileName;
    }

    public void setOrigFileName(String origFileName) {
        this.origFileName = origFileName;
    }

    public String getSysFileName() {
        return sysFileName;
    }

    public void setSysFileName(String sysFileName) {
        this.sysFileName = sysFileName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getJrxmlFile() {
        return jrxmlFile;
    }

    public void setJrxmlFile(byte[] jrxmlFile) {
        this.jrxmlFile = jrxmlFile;
    }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "id=" + id +
                ", version=" + version +
                ", reportName='" + reportName + '\'' +
                ", createdOn=" + createdOn +
                ", createdBy='" + createdBy + '\'' +
                ", origFileName='" + origFileName + '\'' +
                ", sysFileName='" + sysFileName + '\'' +
                ", permission=" + permission +
                ", reportParameters=" + reportParameters +
                '}';
    }
}
