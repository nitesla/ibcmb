package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PendAuth> pendAuths;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public List<PendAuth> getPendAuths() {
        return pendAuths;
    }

    public void setPendAuths(List<PendAuth> pendAuths) {
        this.pendAuths = pendAuths;
    }
}
