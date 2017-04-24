package longbridge.models;

import java.io.IOException;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Created by Wunmi on 27/03/2017.
 */
@MappedSuperclass
public class Beneficiary extends AbstractEntity{

    @ManyToOne
    private RetailUser user;
    private String name;
    private String accountNo;
    private String beneficiaryBank;



    public User getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
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

    @Override
    public String toString() {
        return "Beneficiary{" +
                ", name='" + name + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", beneficiaryBank='" + beneficiaryBank + '\'' +
                '}';
    }

	@Override
	public OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
