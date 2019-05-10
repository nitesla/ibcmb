package longbridge.dtos;

import java.util.Date;
import java.util.List;

public class UserRegDTO {
    private String acctNumber;
    private String userName;
    private String email;
    private String dob;
    private List<String> secQes;
    private List<String> secAns;
    private Date regCodeDate;
    private String regCode;
    private String code;
    private  String password;
    private String confirmPassword;
    private String phising;
    private String caption;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPhising() {
        return phising;
    }

    public void setPhising(String phising) {
        this.phising = phising;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public Date getRegCodeDate() {
        return regCodeDate;
    }

    public void setRegCodeDate(Date regCodeDate) {
        this.regCodeDate = regCodeDate;
    }

    public String getAcctNumber() {
        return acctNumber;
    }

    public void setAcctNumber(String acctNumber) {
        this.acctNumber = acctNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public List<String> getSecQes() {
        return secQes;
    }

    public void setSecQes(List<String> secQes) {
        this.secQes = secQes;
    }

    public List<String> getSecAns() {
        return secAns;
    }

    public void setSecAns(List<String> secAns) {
        this.secAns = secAns;
    }
}
