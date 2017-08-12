package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )

public class BulkTransfer extends TransRequest{

    @Column(unique = true)
    private String refCode;
    private  String status;
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private  CorpTransferAuth transferAuth;

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


    public CorpTransferAuth getTransferAuth() {
        return transferAuth;
    }

    public void setTransferAuth(CorpTransferAuth transferAuth) {
        this.transferAuth = transferAuth;
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
