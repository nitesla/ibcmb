package longbridge.dtos;

public class UpdateCoverageDTO {

    private Long codeId;
    private Long corpId;
    private Long retId;
    private Boolean enabled;

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public Long getCorpId() {
        return corpId;
    }

    public void setCorpId(Long corpId) {
        this.corpId = corpId;
    }

    public Long getRetId() {
        return retId;
    }

    public void setRetId(Long retId) {
        this.retId = retId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "UpdateCoverageDTO{" +
                "codeId=" + codeId +
                ", corpId=" + corpId +
                ", retId=" + retId +
                ", enabled=" + enabled +
                '}';
    }
}
