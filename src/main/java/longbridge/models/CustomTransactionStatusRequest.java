package longbridge.models;

public class CustomTransactionStatusRequest {
    private String Id;
    private String appId;
    private String hash;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
