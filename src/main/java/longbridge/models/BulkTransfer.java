package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class BulkTransfer extends TransRequest{
    private String refCode;
    private  String status;

    @OneToMany(mappedBy = "bulkTransfer",cascade = {CascadeType.ALL})
    private List<CreditRequest> crRequestList;

    @ManyToOne
    private Corporate corporate;



    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }




	public List<CreditRequest> getCrRequestList() {
		return crRequestList;
	}

	public void setCrRequestList(List<CreditRequest> crRequestList) {
		this.crRequestList = crRequestList;
	}

	public String getRefCode() {
		return refCode;
	}

	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public BulkTransfer(String reference, String status, List<CreditRequest> creditRequestList, Corporate corporate) {

        this.refCode = reference;
        this.status = status;
        this.crRequestList = creditRequestList;
        this.corporate = corporate;
    }


    public BulkTransfer() {
    }


}
