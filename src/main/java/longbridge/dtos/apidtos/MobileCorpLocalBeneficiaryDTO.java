package longbridge.dtos.apidtos;

public class MobileCorpLocalBeneficiaryDTO{

    private Long id;
    private String accountName;
    private String accountNumber;
    private String beneficiaryBank;
    private String preferredName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
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

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "MobileCorpLocalBeneficiaryDTO{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", beneficiaryBank='" + beneficiaryBank + '\'' +
                ", preferredName='" + preferredName + '\'' +
                '}';
    }
}
