package longbridge.dtos.apidtos;

import java.io.Serializable;

public class NeftResponseDTO implements Serializable {

    private String appId;
    private String responseCode;
    private String responseMessage;
    private Long msgId;
    private Long itemCount;

    public NeftResponseDTO() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "NeftResponseDTO{" +
                "appId='" + appId + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                ", msgId=" + msgId +
                ", itemCount=" + itemCount +
                '}';
    }
}
