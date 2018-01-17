package longbridge.utils;

/**
 * Created by Fortune on 10/1/2017.
 */
public enum StatusCode {


    PENDING("-01"), PROCESSING("-02"), SUCCESSFUL("00"), CANCELLED("-03"), FAILED("-09");

    private String code;

    StatusCode(String code) {
        this.code = code;
    }

    public String toString() {
        return this.code;
    }

}
