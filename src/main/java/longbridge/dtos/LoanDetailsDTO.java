package longbridge.dtos;

import java.util.List;

public class LoanDetailsDTO {

    private List<LoanDTO> loanList;

    public List<LoanDTO> getLoanList() {
        return loanList;
    }

    public void setLoanList(List<LoanDTO> loanList) {
        this.loanList = loanList;
    }
}
