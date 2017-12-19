package longbridge.api.omnichannel.dto;

/**
 * Created by Fortune on 12/19/2017.
 */
public class ApiError {

    private String error;

    public  ApiError(String error){
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
