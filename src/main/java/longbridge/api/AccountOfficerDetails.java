package longbridge.api;

/**
 * Created by ayoade_farooq@yahoo.com on 3/20/2017.
 */
public class AccountOfficerDetails
{


    private String officerCode;
    private String  officerName;


    public AccountOfficerDetails(String officerCode, String officerName) {
        this.officerCode = officerCode;
        this.officerName = officerName;
    }

    public AccountOfficerDetails() {
    }


    public String getOfficerCode() {
        return officerCode;
    }

    public void setOfficerCode(String officerCode) {
        this.officerCode = officerCode;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }


    @Override
    public String toString() {
        return "{\"AccountOfficerDetails\":{"
                + "                        \"officerCode\":\"" + officerCode + "\""
                + ",                         \"officerName\":\"" + officerName + "\""
                + "}}";
    }
}
