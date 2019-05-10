package longbridge.apiLayer.models;

public enum WebhookResponseCode {

//    SUCCESSFUL ("0"),
    SUCCESSFUL ("0"){
        @Override
    public String toString(){
            return "SUCCESSFUL";
        }

},
    CUSTOMER_DOES_NOT_EXIST ("0");

    private String code;

    WebhookResponseCode(String code) {
        this.code= code;
    }

//    public String toString() {
//        return this.code;
//    }


}
