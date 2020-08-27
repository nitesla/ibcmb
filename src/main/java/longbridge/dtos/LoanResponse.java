package longbridge.dtos;

import java.util.List;



public class LoanResponse {
    private List<LoanDTO> loans;

    public List<LoanDTO> getLoans() {
        return loans;
    }

    public void setLoans(List<LoanDTO> loans) {
        this.loans = loans;
    }
}

