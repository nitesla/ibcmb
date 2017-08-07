package longbridge.models;

import javax.persistence.MappedSuperclass;

/**
 * Created by Wunmi on 27/03/2017.
 */
@MappedSuperclass
public class Beneficiary extends AbstractEntity{

    private String accountName;
    private String accountNumber;
    private String preferredName;
    private String beneficiaryBank;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNo) {
        this.accountNumber = accountNo;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    @Override
    public String toString() {
        return "Beneficiary{" +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", beneficiaryBank='" + beneficiaryBank + '\'' +
                '}';
    }

	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
