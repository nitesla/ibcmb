package longbridge.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fortune on 3/30/2017.
 */
public enum TransferType {


    OWN_ACCOUNT_TRANSFER("Own Account Transfer"),
    CORONATION_BANK_TRANSFER("Coronation Bank Transfer"),
    INTER_BANK_TRANSFER("Interbank Transfer"),
    INTERNATIONAL_TRANSFER("International Transfer"),
    NIP("NIP Transfer"),
    RTGS("RTGS Transfer"),
    NAPS("NAPS Transfer");



    private String description;

    TransferType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }


    public static List<TransferType> getTransferTypes(){
        return Arrays.asList(TransferType.values());
    }
}
