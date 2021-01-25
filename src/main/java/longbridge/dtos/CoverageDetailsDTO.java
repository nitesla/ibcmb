package longbridge.dtos;

import java.util.Set;

public class CoverageDetailsDTO {

    private Set<String> customerIds;
    private String coverageName;
    private String coverageUrl;
    private String customerId;


    public CoverageDetailsDTO() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public String getCoverageUrl() {
        return coverageUrl;
    }

    public void setCoverageUrl(String coverageUrl) {
        this.coverageUrl = coverageUrl;
    }


    @Override
    public String toString() {
        return "CoverageDetailsDTO{" +
                "customerIds=" + customerIds +
                ", coverageName='" + coverageName + '\'' +
                ", coverageUrl='" + coverageUrl + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
