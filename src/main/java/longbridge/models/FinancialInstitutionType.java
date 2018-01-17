package longbridge.models;

/**
 * Created by Wunmi Sowunmi on 25/04/2017.
 */
public enum FinancialInstitutionType {

    LOCAL("LOCAL"),
    FOREIGN("FOREIGN");

    private String description;

    FinancialInstitutionType(String description){
        this.description = description;
    }

}
