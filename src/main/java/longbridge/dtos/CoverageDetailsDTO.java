package longbridge.dtos;

import com.fasterxml.jackson.databind.JsonNode;

public class CoverageDetailsDTO {

    private String customerNumber;
    private String coverageName;
    private JsonNode details;

    public CoverageDetailsDTO() {
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
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

 }
