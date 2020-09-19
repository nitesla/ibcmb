package longbridge.utils;

import java.util.Arrays;
import java.util.List;

public enum NotificationStatus {
    F("Fresh"),
    I("Initiated and expecting response from NCS"),
    P("Successfully notified");

    private final String description;

    NotificationStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public static List<NotificationStatus> getPaymentStatus(){
        return Arrays.asList(NotificationStatus.values());
    }
}
