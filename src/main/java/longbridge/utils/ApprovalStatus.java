package longbridge.utils;

import java.util.Arrays;
import java.util.List;

public enum ApprovalStatus {
    F("Fresh"),
    I("Initiated by another user"),
    A("Approved");

    private final String description;

    ApprovalStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public static List<ApprovalStatus> getPaymentStatus(){
        return Arrays.asList(ApprovalStatus.values());
    }

}
