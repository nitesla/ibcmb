package longbridge.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Fortune on 3/29/2017.
 */
public class CorporateCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String rcNumber;
    private String companyName;
    private String email;
    private String address;
}
