package longbridge.models;

import javax.persistence.*;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Customer {

    @Id
    private Long Id;

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Long customerType_Id;
    private Long corporateCustomer_Id;
    private Long role_Id;
    private Long profile_Id;


}
