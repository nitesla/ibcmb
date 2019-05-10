package longbridge.apiLayer.data;

public class ResponseData {
    private String message;
    private Object data;
    private String code;
    private Boolean error;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                ", error=" + error +
                '}';
    }

    public String customApiString() {
        return "{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                ", error=" + error +
                '}';
    }
}
