package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomAssessmentDetail {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Message")
    private String message;

    public CustomAssessmentDetail() {
    }

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomAssessmentDetail{" +
                "responseInfo=" + responseInfo +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
