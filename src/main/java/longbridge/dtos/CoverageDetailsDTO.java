package longbridge.dtos;

import com.fasterxml.jackson.databind.JsonNode;

public class CoverageDetailsDTO {

    private String customerId;
    private String coverageName;
    private JsonNode details;

    public CoverageDetailsDTO() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCoverageName() {
        return coverageName;
    }

    public void setCoverageName(String coverageName) {
        this.coverageName = coverageName;
    }

    public JsonNode getDetails() {
        return details;
    }

    public void setDetails(JsonNode details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "CoverageDetailsDTO{" +
                "customerId='" + customerId + '\'' +
                ", coverageName='" + coverageName + '\'' +
                ", details=" + details +
                '}';
    }
}
