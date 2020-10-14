package longbridge.utils;

import java.util.Arrays;
import java.util.List;

/**The {@code TransferType} enum contains the various types of transfers
 * @author Fortunatus Eekenachi
 * Created on 3/30/2017.
 */
public enum TransferType {


    OWN_ACCOUNT_TRANSFER("Own Account Transfer"),
    CORONATION_BANK_TRANSFER("Coronation Bank Transfer"),
    INTER_BANK_TRANSFER("Interbank Transfer"),
    INTERNATIONAL_TRANSFER("International Transfer"),
    NIP("NIP Transfer"),
    NAPS("NAPS Transfer"),
    RTGS("RTGS Transfer"),
    NEFT("NEFT Transfer"),
    CUSTOM_DUTY("Custom Duty Payment"),
    QUICKTELLER("QUICKTELLER Transfer");



    private final String description;

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
