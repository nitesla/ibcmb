package longbridge.dtos.apidtos;

public class MobileCorporateUserDTO {

    private String corporateName;
    private String rcNumber;
    private String lastLoginDate;


    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public String toString() {
        return "{\"MobileRetailUserDTO\":"
                + super.toString()
                + ",                         \"corporateName\":\"" + corporateName + "\""
                + ",                         \"rcNumber\":\"" + rcNumber + "\""
                + ",                         \"lastLoginDate\":\"" + lastLoginDate + "\""
                + "}";
    }
}
