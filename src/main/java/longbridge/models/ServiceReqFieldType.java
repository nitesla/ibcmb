package longbridge.models;

/**
 * Created by Wunmi on 08/04/2017.
 */
public enum ServiceReqFieldType {

    TEXT("TEXT"),
    CODE("CODE"),
    ACCOUNT("ACCOUNT"),
    DATE("DATE"),
    FIXEDLIST("FIXEDLIST");


    private String description;

    ServiceReqFieldType(String description){
        this.description = description;
    }

}
