package longbridge.apiLayer.models;

public enum WebhookResponseCode {

//    SUCCESSFUL ("0"),
    SUCCESSFUL (){
        @Override
    public String toString(){
            return "SUCCESSFUL";
        }

},
    CUSTOMER_DOES_NOT_EXIST ();

    WebhookResponseCode() {
    }

//    public String toString() {
//        return this.code;
//    }


}
