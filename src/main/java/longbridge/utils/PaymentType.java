package longbridge.utils;

import java.util.Arrays;
import java.util.List;

public enum PaymentType {

    CUSTOM_DUTY("Custom Duty"),
    UTILITY("Utility");

    private final String description;

    PaymentType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }


    public static List<PaymentType> getPaymentTypes(){
        return Arrays.asList(PaymentType.values());
    }
}
