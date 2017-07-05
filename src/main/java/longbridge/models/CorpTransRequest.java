package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpTransRequest extends TransRequest {

    @ManyToOne
    private Corporate corporate;

    @OneToOne
    private  CorpTransferAuth transferAuth;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public CorpTransferAuth getTransferAuth() {
        return transferAuth;
    }

    public void setTransferAuth(CorpTransferAuth transferAuth) {
        this.transferAuth = transferAuth;
    }
}