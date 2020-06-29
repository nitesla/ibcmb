package longbridge.dtos;

import org.json.simple.JSONObject;

public class AccountCoverageDTO {
    private Long id;
    private String code;
    private String description;
    private boolean enabled;
    private Long corpId;
    private Long codeId;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public Long getCorpId() {
        return corpId;
    }

    public void setCorpId(Long corpId) {
        this.corpId = corpId;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }



    @Override
    public String toString() {
        return "AccountCoverageDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", corpId=" + corpId +
                ", codeId=" + codeId +
                 '}';
    }

}
