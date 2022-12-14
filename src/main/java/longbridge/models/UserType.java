package longbridge.models;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chigozirim on 3/31/17.
 */
public enum UserType {
    ADMIN("ADM"),
    OPERATIONS("OPS"),
    RETAIL("RET"),
    CORPORATE("CORP");

    UserType(String description){
    }

    public static List<UserType> getUseryTypes(){
        return Arrays.asList(UserType.values());
    }

}
