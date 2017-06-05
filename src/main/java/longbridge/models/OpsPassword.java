package longbridge.models;


import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Created by Fortune on 6/5/2017.
 */


@Entity
@Where(clause ="del_Flag='N'" )
public class OpsPassword  extends  AbstractEntity{

    OperationsUser operationsUser;
    String password;

    public OperationsUser getOperationsUser() {
        return operationsUser;
    }

    public void setOperationsUser(OperationsUser operationsUser) {
        this.operationsUser = operationsUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
