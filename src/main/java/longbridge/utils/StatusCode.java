package longbridge.utils;

/**
 * Created by Fortune on 10/1/2017.
 */
public enum StatusCode {


    PENDING("-01"){
        public String toString(){
            return "Pending";
        }
    },
    PROCESSING("-02"){
        public String toString(){
            return "Processing";
        }
    },

    SUCCESSFUL("00"){
        public String toString(){
            return "Successful";
        }
    },

    COMPLETED("01"){
        public String toString(){
            return "Completed";
        }
    },

    CANCELLED("-03"){
        public String toString(){
            return "Cancelled";
        }
    },
    FAILED("-09"){
        public String toString(){
            return "Failed Debit";
        }
    };

    private final String code;

    StatusCode(String code) {
        this.code = code;
    }

    public String getStatusCode(){
        return code;
    }

}
