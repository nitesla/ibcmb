package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomSADAsmt {

    @JsonProperty("SADAssessmentSerial")
    private String SADAssessmentSerial;

    @JsonProperty("SADAssessmentNumber")
    private String SADAssessmentNumber;

    @JsonProperty("SADYear")
    private String SADYear;

//    public CustomSADAsmt() {
//    }

    public String getSADAssessmentSerial() {
        return SADAssessmentSerial;
    }

    public void setSADAssessmentSerial(String SADAssessmentSerial) {
        this.SADAssessmentSerial = SADAssessmentSerial;
    }

    public String getSADAssessmentNumber() {
        return SADAssessmentNumber;
    }

    public void setSADAssessmentNumber(String SADAssessmentNumber) {
        this.SADAssessmentNumber = SADAssessmentNumber;
    }

    public String getSADYear() {
        return SADYear;
    }

    public void setSADYear(String SADYear) {
        this.SADYear = SADYear;
    }

    @Override
    public String toString() {
        return "CustomSADAsmt{" +
                "SADAssessmentSerial='" + SADAssessmentSerial + '\'' +
                ", SADAssessmentNumber='" + SADAssessmentNumber + '\'' +
                ", SADYear='" + SADYear + '\'' +
                '}';
    }
}
