package longbridge.dtos;



public class CoverageDTO {
    private Long id;
    private String code;
    private String description;
    private boolean enabled;
    private Long corpId;
    private Long codeId;
    private Long retId;


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

    public Long getRetId() {
        return retId;
    }

    public void setRetId(Long retId) {
        this.retId = retId;
    }

    @Override
    public String toString() {
        return "CoverageDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", corpId=" + corpId +
                ", codeId=" + codeId +
                ", retId=" + retId +
                '}';
    }

}
