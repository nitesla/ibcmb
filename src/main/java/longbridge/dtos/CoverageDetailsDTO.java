package longbridge.dtos;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public class CoverageDetailsDTO {

    private Set<String> customerIds;
    private String coverageName;
    private JsonNode coverageDetails;


    public CoverageDetailsDTO() {
    }

    public Set<String> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(Set<String> customerIds) {
        this.customerIds = customerIds;
    }

    public String getCoverageName() {
        return coverageName;
    }

    public void setCoverageName(String coverageName) {
        this.coverageName = coverageName;
    }

    public JsonNode getCoverageDetails() {
        return coverageDetails;
    }

    public void setCoverageDetails(JsonNode coverageDetails) {
        this.coverageDetails = coverageDetails;
    }

    @Override
    public String toString() {
        return "CoverageDetailsDTO{" +
                "customerId='" + customerIds + '\'' +
                ", coverageName='" + coverageName + '\'' +
                ", coverageDetails=" + coverageDetails +
                '}';
    }
}
