package longbridge.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Wunmi Sowunmi on 25/04/2017.
 */
public class AlertPref {

    @NotEmpty
    private String preference;

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

}
