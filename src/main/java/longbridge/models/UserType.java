package longbridge.models;

/**
 * Created by chigozirim on 3/31/17.
 */
public enum UserType {
    ADMIN("ADM"),
    OPERATIONS("OPS"),
    RETAIL("RET"),
    CORPORATE("CORP");


    private String description;

    UserType(String description){
        this.description = description;
    }

}
