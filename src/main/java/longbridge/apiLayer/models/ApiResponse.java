package longbridge.apiLayer.models;

public class ApiResponse {

    public Boolean error;
    public String message;
    public Object data;
    public String code;


    public ApiResponse(String code, Boolean error, String message, Object data) {
        this.error = error;
        this.message = message;
        this.data = data;
        this.code = code;

    }
}
