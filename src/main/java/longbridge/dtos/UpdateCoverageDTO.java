package longbridge.dtos;

public class UpdateCoverageDTO {

    private Long codeId;
    private Long corpId;
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
                ", enabled=" + enabled +
                '}';
    }
}
