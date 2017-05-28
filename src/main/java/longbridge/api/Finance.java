package longbridge.api;

/**
 * Created by ayoade_farooq@yahoo.com on 5/27/2017.
 */
public class Finance {

    private  String institutionCode;
    private String institutionName;
    private String  category;

    public Finance() {
    }

    public Finance(String institutionCode, String institutionName, String category) {
        this.institutionCode = institutionCode;
        this.institutionName = institutionName;
        this.category = category;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
