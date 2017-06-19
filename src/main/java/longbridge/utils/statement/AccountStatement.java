package longbridge.utils.statement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 6/15/2017.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountStatement{

    private AccountStatementWithPaginationCustomData accountStatementWithPaginationCustomData;
    private PaginatedAccountStatement paginatedAccountStatement;

    public AccountStatementWithPaginationCustomData getAccountStatementWithPaginationCustomData() {
        return accountStatementWithPaginationCustomData;
    }

    public void setAccountStatementWithPaginationCustomData(AccountStatementWithPaginationCustomData accountStatementWithPaginationCustomData) {
        this.accountStatementWithPaginationCustomData = accountStatementWithPaginationCustomData;
    }

    public PaginatedAccountStatement getPaginatedAccountStatement() {
        return paginatedAccountStatement;
    }

    public void setPaginatedAccountStatement(PaginatedAccountStatement paginatedAccountStatement) {
        this.paginatedAccountStatement = paginatedAccountStatement;
    }
}
