package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 29/01/2018.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Report extends AbstractEntity implements PrettySerializer {
    private String reportName;
    private Date createdOn;
    private String createdBy;
    private String origFileName;
    private String sysFileName;
    @Lob
    @Column(length = 100000)
    private byte[] jrxmlFile;
    @OneToOne
    private Permission permission;
    @OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
    private List<ReportParameters> reportParameters;

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

    public List<ReportParameters> getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(List<ReportParameters> reportParameters) {
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

    public byte[] getJrxmlFile() {
        return jrxmlFile;
    }

    public void setJrxmlFile(byte[] jrxmlFile) {
        this.jrxmlFile = jrxmlFile;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportName='" + reportName + '\'' +
                ", createdOn=" + createdOn +
                ", createdBy='" + createdBy + '\'' +
                ", origFileName='" + origFileName + '\'' +
                ", sysFileName='" + sysFileName + '\'' +
                ", permission=" + permission +
                ", reportParameters=" + reportParameters +
                '}';
    }


    @Override
    @JsonIgnore
    public JsonSerializer<Report> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Report value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {

                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("reportName", value.reportName);
                gen.writeStringField("createdBy", value.createdBy);
                if (value.createdOn != null) {
                    gen.writeStringField("createdOn", value.createdOn.toString());
                } else {
                    gen.writeStringField("createdOn", "");
                }
                gen.writeStringField("origFileName", value.origFileName);

                // gen.writeArrayFieldStart("permissions");
                gen.writeObjectFieldStart("permission");
                if (value.getPermission() != null) {
                    gen.writeObjectFieldStart(value.getPermission().getId().toString());
                    //gen.writeStartObject();
                    gen.writeStringField("name", value.getPermission().getName());
                    gen.writeStringField("code", value.getPermission().getCode());
                    gen.writeStringField("category", value.getPermission().getCategory());
                    gen.writeStringField("description", value.getPermission().getDescription());
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                gen.writeObjectFieldStart("reportParameters");
                for (ReportParameters p : value.getReportParameters()) {
                    if (p.getId() != null) {
                        gen.writeObjectFieldStart(p.getId().toString());
                        if (p.getId() != null) {
                            gen.writeStringField("id", p.getId().toString());
                        } else {
                            gen.writeStringField("id", "");
                        }
                        //gen.writeStartObject();
                        gen.writeStringField("datatype", p.getDatatype());
                        gen.writeStringField("parameterName", p.getParameterName());
                        gen.writeStringField("parameterDesc", p.getParameterDesc());
                        gen.writeEndObject();
                    }
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }@Override
    @JsonIgnore
    public JsonSerializer<Report> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Report value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {

                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("reportName", value.reportName);
                gen.writeStringField("createdBy", value.createdBy);
                if (value.createdOn != null) {
                    gen.writeStringField("createdOn", value.createdOn.toString());
                } else {
                    gen.writeStringField("createdOn", "");
                }
                gen.writeStringField("origFileName", value.origFileName);

                // gen.writeArrayFieldStart("permissions");
                gen.writeObjectFieldStart("permission");
                if (value.getPermission() != null) {
                    gen.writeObjectFieldStart(value.getPermission().getId().toString());
                    //gen.writeStartObject();
                    gen.writeStringField("name", value.getPermission().getName());
                    gen.writeStringField("code", value.getPermission().getCode());
                    gen.writeStringField("category", value.getPermission().getCategory());
                    gen.writeStringField("description", value.getPermission().getDescription());
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                gen.writeObjectFieldStart("reportParameters");
                if (value.getReportParameters() != null) {
                    for (ReportParameters p : value.getReportParameters()) {
                        if (p.getId() != null) {
                            gen.writeObjectFieldStart(p.getId().toString());
                            if (p.getId() != null) {
                                gen.writeStringField("id", p.getId().toString());
                            } else {
                                gen.writeStringField("id", "");
                            }
                            //gen.writeStartObject();
                            gen.writeStringField("datatype", p.getDatatype());
                            gen.writeStringField("parameterName", p.getParameterName());
                            gen.writeStringField("parameterDesc", p.getParameterDesc());
                            gen.writeEndObject();
                        }
                    }
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }
    @Override
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("reportName", "createdBy");
    }

}
