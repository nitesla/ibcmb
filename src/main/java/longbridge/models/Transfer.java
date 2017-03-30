package longbridge.models;

import longbridge.utils.TransferType;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Fortune on 3/30/2017.
 */
@Entity
public class Transfer extends AbstractEntity{

    private  Account account;

    private TransferType transferType;

    @ManyToOne(fetch = FetchType.LAZY,optional = false,cascade = CascadeType.ALL)
    private FinancialInstitution financialInstitution;

    private String beneficiaryAccountNumber;

    private String beneficiaryAccountName;

    private String remarks;

    private String referenceNumber;

    private String narration;

    private String sessionId;
}
