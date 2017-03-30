package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class CorporateUser extends User {

    private Long corporateCustomer_Id;

    @Override
    public String toString() {
        return "CorporateUser{" +
                ", corporateCustomer_Id=" + corporateCustomer_Id +
                '}';
    }


    public Long getCorporateCustomer_Id() {
        return corporateCustomer_Id;
    }

    public void setCorporateCustomer_Id(Long corporateCustomer_Id) {
        this.corporateCustomer_Id = corporateCustomer_Id;
    }


}
