package longbridge.models;

import longbridge.utils.TransferType;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class CorpTransferRequest extends TransferRequest {


    @ManyToOne
    private Corporate corporate;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PendingAuthorization> pendingAuthorizations;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public List<PendingAuthorization> getPendingAuthorizations() {
        return pendingAuthorizations;
    }

    public void setPendingAuthorizations(List<PendingAuthorization> pendingAuthorizations) {
        this.pendingAuthorizations = pendingAuthorizations;
    }
}
