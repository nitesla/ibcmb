package longbridge.dtos;

import javax.validation.constraints.NotEmpty;

public class NeftBeneficiaryDTO {
    private Long id;
    @NotEmpty(message = "Please enter a Beneficiary Name")
    private String beneficiaryAccName;
    @NotEmpty(message = "Please enter an Account Number")
    private String beneficiaryAccNo;
    @NotEmpty(message = "Please enter a Beneficiary Bank")
    private String beneficiarySortCode;
    @NotEmpty(message = "Please enter a Beneficiary BVN Number")
    private String beneficiaryBVN;
    private String beneficiaryBankName;

    public NeftBeneficiaryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeneficiaryAccName() {
        return beneficiaryAccName;
    }

    public void setBeneficiaryAccName(String beneficiaryAccName) {
        this.beneficiaryAccName = beneficiaryAccName;
    }

    public String getBeneficiaryAccNo() {
        return beneficiaryAccNo;
    }

    public void setBeneficiaryAccNo(String beneficiaryAccNo) {
        this.beneficiaryAccNo = beneficiaryAccNo;
    }

    public String getBeneficiarySortCode() {
        return beneficiarySortCode;
    }

    public void setBeneficiarySortCode(String beneficiarySortCode) {
        this.beneficiarySortCode = beneficiarySortCode;
    }


    public String getBeneficiaryBVN() {
        return beneficiaryBVN;
    }

    public void setBeneficiaryBVN(String beneficiaryBVN) {
        this.beneficiaryBVN = beneficiaryBVN;
    }

    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }


    @Override
    public String toString() {
        return "NeftBeneficiaryDTO{" +
                "id=" + id +
                ", beneficiaryAccName='" + beneficiaryAccName + '\'' +
                ", beneficiaryAccNo='" + beneficiaryAccNo + '\'' +
                ", beneficiarySortCode='" + beneficiarySortCode + '\'' +
                ", beneficiaryBVN='" + beneficiaryBVN + '\'' +
                ", beneficiaryBankName='" + beneficiaryBankName + '\'' +
                '}';
    }
}
