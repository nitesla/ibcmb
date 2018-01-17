package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.AbstractEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;

/**
 * Created by Fortune on 4/5/2017.
 *
 */

public class CodeDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty(message = "code")
    private String code;
    @NotEmpty(message = "type")
    private String type;
    @NotEmpty(message = "description")
    private String description;

    private String extraInfo;
    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CodeDTO{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
