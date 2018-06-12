package longbridge.dtos;

import longbridge.models.Account;

public class AccountPermissionDTO {

    private String accountNumber;
    private String customerId;
    private Permission permission;


    public AccountPermissionDTO() {
    }

    public AccountPermissionDTO(Account account, Permission permission) {
        this.accountNumber = account.getAccountNumber();
        this.customerId = account.getCustomerId();
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountPermissionDTO)) return false;

        AccountPermissionDTO that = (AccountPermissionDTO) o;

        return accountNumber.equals(that.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    public enum Permission {
        VIEW_ONLY, VIEW_AND_TRANSACT, NONE
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "AccountPermissionDTO{" +
                "accountNumber='" + accountNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", permission=" + permission +
                '}';
    }
}
