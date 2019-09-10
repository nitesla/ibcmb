package longbridge.utils;

/**
 * Created by Shina on 10/1/2017.
 */
public enum CustomDutyCode {

    SUCCESSFUL("00"){
        @Override
        public String toString() {
            return "Successful";
        }
    },
    SUSPECTED_FRAUD("34"){
        @Override
        public String toString() {
            return "Failed";
        }
    },
    INVALID_ACCOUNT("02")
            {
                @Override
                public String toString() {
                    return "Failed";
                }
            },
    INVALID_AMOUNT("04"){
        @Override
        public String toString() {
            return "Failed";
        }
    },
    SYSTEM_FAILURE("96"){
        @Override
        public String toString() {
            return "Failed";
        }
    }
    , DUPLICATE_TRANSACTION("94"){
        @Override
        public String toString() {
            return "Duplicate Transaction";
        }
    },
    INVALID_MERCHANT("03"){
        @Override
        public String toString() {
            return "Failed";
        }
    },
    INVALID_ASSESSMENT("01"){
        @Override
        public String toString() {
            return "Failed";
        }
    },
    FAILED_DEBIT("-1"){
        @Override
        public String toString() {
            return "Failed debit";
        }
    },
    FRESH("F"){
        @Override
        public String toString() {
            return "Pending";
        }
    },DEBIT_FAILED("X"){
        @Override
        public String toString() {
            return "Failed";
        }
    },
    ACCOUNT_DEBITED_SUCCESSFULlY("P"){
        @Override
        public String toString() {
            return "Successful";
        }
    };

    private String code;

    CustomDutyCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getCustomDutyCodeByCode(String code){
        CustomDutyCode[] codes = CustomDutyCode.values();
        for (CustomDutyCode customDutyCode:codes) {
            if(customDutyCode.getCode().equals(code)){
            return customDutyCode.toString();
            }
        }
        return SYSTEM_FAILURE.toString();
    }

    public static String getCustomDutyCodeByCodeForOPS(String code){
        CustomDutyCode[] codes = CustomDutyCode.values();
        for (CustomDutyCode customDutyCode:codes) {
            if("".equals(code)|| null==code){
                return "";
            }
            if(customDutyCode.getCode().equals(code)){
                return customDutyCode.name();
            }
        }
        return SYSTEM_FAILURE.name();
    }


}
