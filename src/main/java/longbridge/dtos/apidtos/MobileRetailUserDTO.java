package longbridge.dtos.apidtos;


import java.io.Serializable;

public class MobileRetailUserDTO implements Serializable {

    private String userName;
    private String firstName;
    private String lastName;
    private String bvn;
    private String lastLoginDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }


    @Override
    public String toString() {
        return "{\"MobileRetailUserDTO\":"
                + super.toString()
                + ",                         \"userName\":\"" + userName + "\""
                + ",                         \"firstName\":\"" + firstName + "\""
                + ",                         \"lastName\":\"" + lastName + "\""
                + ",                         \"bvn\":\"" + bvn + "\""
                + ",                         \"lastLoginDate\":\"" + lastLoginDate + "\""
                + "}";
    }
}
