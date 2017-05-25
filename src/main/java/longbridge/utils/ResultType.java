package longbridge.utils;

/**
 * Created by ayoade_farooq@yahoo.com on 3/22/2017.
 */
public enum ResultType {


    SUCCESS("SUCCESS"), INFO("INFO"), WARNING("WARNING"), ERROR("ERROR");

    private  String name="";

    ResultType(String s) {
        name=s;
      }
    public String toString() {
        return this.name;
    }
}
