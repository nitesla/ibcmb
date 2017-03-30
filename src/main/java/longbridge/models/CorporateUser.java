package longbridge.models;

import javax.persistence.*;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class CorporateUser extends AbstractEntity{


    private String rcNumber;
    private String companyName;
    private String email;
    private String address;

}
