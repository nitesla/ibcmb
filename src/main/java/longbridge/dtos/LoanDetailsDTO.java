package longbridge.dtos;

import java.io.Serializable;
import java.util.List;

public class LoanDetailsDTO implements Serializable {

    private List<LoanDTO> loanList;

    public List<LoanDTO> getLoanList() {
        return loanList;
    }

    public void setLoanList(List<LoanDTO> loanList) {
        this.loanList = loanList;
    }
}
