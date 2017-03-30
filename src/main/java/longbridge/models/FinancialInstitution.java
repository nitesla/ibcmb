package longbridge.models;

import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Created by Fortune on 3/30/2017.
 */
public class FinancialInstitution extends AbstractEntity {


    private String institutionCode;

    private String institutionName;

    @OneToMany(mappedBy = "financialInstitution")
    private Transfer transfer;
}

