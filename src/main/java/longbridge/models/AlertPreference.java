package longbridge.models;

/**
 * Created by Wunmi Sowunmi on 25/04/2017.
 */
public enum AlertPreference {
    BOTH("BOTH"),
    EMAIL("EMAIL"),
    SMS("SMS"),
    NONE("NONE");

    private String description;

    AlertPreference(String description){
        this.description = description;
    }
}
