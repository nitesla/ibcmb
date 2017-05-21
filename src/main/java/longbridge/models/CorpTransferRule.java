package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */

@Entity
@Audited
@Where(clause = "del_Flag='N'")
public class CorpTransferRule extends AbstractEntity {

    private BigDecimal lowerLimitAmount;
    private BigDecimal upperLimitAmount;
    private boolean infinite;
    private boolean anyOne;

    @ManyToOne
    private Corporate corporate;

    @ManyToMany
    private List<CorporateUser> authorizers;

    public BigDecimal getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(BigDecimal lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public BigDecimal getUpperLimitAmount() {
        return upperLimitAmount;
    }

    public void setUpperLimitAmount(BigDecimal upperLimitAmount) {
        this.upperLimitAmount = upperLimitAmount;
    }

    public boolean isAnyOne() {
        return anyOne;
    }

    public void setAnyOne(boolean anyOne) {
        this.anyOne = anyOne;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public List<CorporateUser> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(List<CorporateUser> authorizers) {
        this.authorizers = authorizers;
    }
}
