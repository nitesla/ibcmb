package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Created by mac on 23/02/2018.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpDirectDebit extends DirectDebit {

    @ManyToOne
    CorporateUser corporateUser;

    Long corporate ;

    @ManyToOne
    CorpLocalBeneficiary corpLocalBeneficiary;

    public Long getCorporate() {
        return corporate;
    }

    public void setCorporate(Long corporate) {
        this.corporate = corporate;
    }

    public CorporateUser getCorporateUser() {
        return corporateUser;
    }

    public void setCorporateUser(CorporateUser corporateUser) {
        this.corporateUser = corporateUser;
    }

    public CorpLocalBeneficiary getCorpLocalBeneficiary() {
        return corpLocalBeneficiary;
    }

    public void setCorpLocalBeneficiary(CorpLocalBeneficiary corpLocalBeneficiary) {
        this.corpLocalBeneficiary = corpLocalBeneficiary;
    }


    @Override
    public String toString() {
        return "CorpDirectDebit{" +
                "corporateUser=" + corporateUser +
                ", corporate=" + corporate +
                ", corpLocalBeneficiary=" + corpLocalBeneficiary +
                ", debitAccount='" + debitAccount + '\'' +
                ", amount=" + amount +
                ", intervalDays=" + intervalDays +
                ", id=" + id +
                '}';
    }



}
