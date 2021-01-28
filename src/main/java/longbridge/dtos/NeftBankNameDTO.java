package longbridge.dtos;

 import java.io.Serializable;

public class NeftBankNameDTO implements Serializable {

    private String bankName;

    public NeftBankNameDTO(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return "NeftBankNameDTO{" +
                "bankName='" + bankName + '\'' +
                '}';
    }
}
