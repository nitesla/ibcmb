package longbridge.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 3/20/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetails {

    private String solId;
    private String acctOpenDate;
    private String acctCrncyCode;
    private String freezeCode;
    private String acctStatus;
    private String acctType;
    private String unClrBalAmt;
    private String ledgerBalAmt;
    private String sanctLim;
    private String drwngPower;
    private String acctName;
    private String acctNumber;
    private String schmCode;
    private String availableBalance;
    private String custId;


    public AccountDetails() {
    }

    public String getSolId() {
        return solId;
    }

    public void setSolId(String solId) {
        this.solId = solId;
    }

    public String getAcctOpenDate() {
        return acctOpenDate;
    }

    public void setAcctOpenDate(String acctOpenDate) {
        this.acctOpenDate = acctOpenDate;
    }

    public String getAcctCrncyCode() {
        return acctCrncyCode;
    }

    public void setAcctCrncyCode(String acctCrncyCode) {
        this.acctCrncyCode = acctCrncyCode;
    }

    public String getFreezeCode() {
        return freezeCode;
    }

    public void setFreezeCode(String freezeCode) {
        this.freezeCode = freezeCode;
    }

    public String getAcctStatus() {
        return acctStatus;
    }

    public void setAcctStatus(String acctStatus) {
        this.acctStatus = acctStatus;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }


    public String getUnClrBalAmt() {
        return unClrBalAmt;
    }

    public void setUnClrBalAmt(String unClrBalAmt) {
        this.unClrBalAmt = unClrBalAmt;
    }

    public String getLedgerBalAmt() {
        return ledgerBalAmt;
    }

    public void setLedgerBalAmt(String ledgerBalAmt) {
        this.ledgerBalAmt = ledgerBalAmt;
    }

    public String getSanctLim() {
        return sanctLim;
    }

    public void setSanctLim(String sanctLim) {
        this.sanctLim = sanctLim;
    }

    public String getDrwngPower() {
        return drwngPower;
    }

    public void setDrwngPower(String drwngPower) {
        this.drwngPower = drwngPower;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getAcctNumber() {
        return acctNumber;
    }

    public void setAcctNumber(String acctNumber) {
        this.acctNumber = acctNumber;
    }


    public String getSchmCode() {
        return schmCode;
    }

    public void setSchmCode(String schmCode) {
        this.schmCode = schmCode;
    }



    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }




    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    @Override
    public String toString() {
        return "{\"AccountDetails\":{"
                + "                        \"solId\":\"" + solId + "\""
                + ",                         \"acctOpenDate\":\"" + acctOpenDate + "\""
                + ",                         \"acctCrncyCode\":\"" + acctCrncyCode + "\""
                + ",                         \"freezeCode\":\"" + freezeCode + "\""
                + ",                         \"acctStatus\":\"" + acctStatus + "\""
                + ",                         \"acctType\":\"" + acctType + "\""
                + ",                         \"unClrBalAmt\":\"" + unClrBalAmt + "\""
                + ",                         \"ledgerBalAmt\":\"" + ledgerBalAmt + "\""
                + ",                         \"sanctLim\":\"" + sanctLim + "\""
                + ",                         \"drwngPower\":\"" + drwngPower + "\""
                + ",                         \"acctName\":\"" + acctName + "\""
                + ",                         \"acctNumber\":\"" + acctNumber + "\""
                + ",                         \"schmCode\":\"" + schmCode + "\""
                + ",                         \"availableBalance\":\"" + availableBalance + "\""
                + ",                         \"custId\":\"" + custId + "\""
                + "}}";
    }
}
