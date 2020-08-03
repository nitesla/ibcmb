package longbridge.dtos;

import org.json.simple.JSONObject;

public class AccountCoverageDTO {
    private Long id;
    private String code;
    private String description;
    private boolean isEnabled;
    private JSONObject details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public JSONObject getDetails() {
        return details;
    }

    public void setDetails(JSONObject details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "AccountCoverageDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", isEnabled=" + isEnabled +
                ", details=" + details +
                '}';
    }
}
