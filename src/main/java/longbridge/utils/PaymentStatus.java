package longbridge.utils;

import java.util.Arrays;
import java.util.List;

public enum PaymentStatus {
    F("Fresh"),
    P("Account Debited Successfully"),
    X("Debit Failed");

    private final String description;

    PaymentStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public static List<PaymentStatus> getPaymentStatus(){
        return Arrays.asList(PaymentStatus.values());
    }
}
