package longbridge.models;

/**
 * Created by Wunmi on 27/03/2017.
 */
public abstract class Beneficiary extends AbstractEntity{


    private String ownerId;
    private String name;

    private String accountNo;
    private String beneficiaryBank;

    @Override
    public String toString() {
        return "Beneficiary{" +
                "ownerId='" + ownerId + '\'' +
                ", name='" + name + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", beneficiaryBank='" + beneficiaryBank + '\'' +
                '}';
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }
}
