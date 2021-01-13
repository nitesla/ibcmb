package longbridge.dtos;

import java.util.List;

public class UpdateCoverageCmd {
    private List<String> coverages;
    private Long entityId;



    public List<String> getCoverages() {
        return coverages;
    }

    public void setCoverages(List<String> coverages) {
        this.coverages = coverages;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
