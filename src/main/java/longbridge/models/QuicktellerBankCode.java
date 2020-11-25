package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.List;


@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"cbnCode","bankName","bankCode"}))
public class QuicktellerBankCode extends AbstractEntity{


    private String bankCodeId;

    private String cbnCode;

    private String bankName;

    private String bankCode;

    public String getBankCodeId() {
        return bankCodeId;
    }

    public void setBankCodeId(String bankCodeId) {
        this.bankCodeId = bankCodeId;
    }

    public String getCbnCode() {
        return cbnCode;
    }

    public void setCbnCode(String cbnCode) {
        this.cbnCode = cbnCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    public String toString() {
        return "QuicktellerBankCode{" +
                "bankCodeId='" + bankCodeId + '\'' +
                ", cbnCode='" + cbnCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }

    @Override
   	public List<String> getDefaultSearchFields() {
        return Arrays.asList("cbnCode","bankCodeId","bankName","bankCode");
   	}


}

