package longbridge.exception;

/**
 * Created by ayoade_farooq@yahoo.com on 5/25/2017.
 */
public enum TransferExceptions {
    BALANCE("transfer.balance.insufficient"), SAME_ACCOUNT("transfer.account.same"), NO_DEBIT_ACCOUNT("transfer.debit.invalid"), ERROR("transfer.send.failure")
    ,INVALID_ACCOUNT("transfer.account.invalid"), INVALID_BENEFICIARY("transfer.beneficiary.invalid");


    ;

    private  String name="";

    TransferExceptions(String s) {
        name=s;
    }
    public String toString() {
        return this.name;
    }


}
