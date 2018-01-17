package longbridge.models;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Showboy on 28/07/2017.
 */
public enum CorpUserType {

    ADMIN("ADM"),
    INITIATOR("INIT"),
    AUTHORIZER("AUTH");

    private String description;

    CorpUserType(String description){
        this.description = description;
    }

    public static List<CorpUserType> getUseryTypes(){
        return Arrays.asList(CorpUserType.values());
    }

}