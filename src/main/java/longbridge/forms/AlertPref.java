package longbridge.forms;

import longbridge.models.AlertPreference;

import javax.validation.constraints.NotNull;

/**
 * Created by Wunmi Sowunmi on 25/04/2017.
 */
public class AlertPref {

    @NotNull
    private AlertPreference preference;

    public AlertPreference getPreference() {
        return preference;
    }

    public void setPreference(AlertPreference preference) {
        this.preference = preference;
    }
}
