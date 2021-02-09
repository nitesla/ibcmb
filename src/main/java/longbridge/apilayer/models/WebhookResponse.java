package longbridge.apilayer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookResponse {

    @JsonProperty("code")
    String code;
    String description;

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
}
