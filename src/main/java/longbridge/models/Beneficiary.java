package longbridge.models;

import javax.persistence.*;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Beneficiary extends AbstractEntity{

    private String ownerId;
    private String name;
    private String beneficiaryType;
    private String accountNo;
    private String beneficiaryBank;

}
