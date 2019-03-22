package longbridge.utils;

/**
 * Created by Shina on 10/1/2017.
 */
public enum CustomDutyCode {

    SUCCESSFUL("00"),SUSPECTED_FRAUD("34"), INVALID_ACCOUNT("02"), INVALID_AMOUNT("04"), SYSTEM_FAILURE("96")
    , DUPLICATE_TRANSACTION("94"), INVALID_MERCHANT("03"), INVALID_ASSESSMENT("01"),FAILED_DEBIT("-1"),FRESH("F"),DEBIT_FAILED("X"),ACCOUNT_DEBITED_SUCCESSFULlY("P");

    private String code;

     CustomDutyCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.name();
    }


    public String getCode() {
        return code;
    }

    public static String getCustomDutyCodeByCode(String code){
        CustomDutyCode[] codes = CustomDutyCode.values();
        for (CustomDutyCode customDutyCode:codes) {
            if(customDutyCode.getCode().equals(code)){
            return customDutyCode.name();
            }
        }
        return SYSTEM_FAILURE.toString();
    }

}
