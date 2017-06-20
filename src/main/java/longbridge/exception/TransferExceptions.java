package longbridge.exception;

/**
 * Created by ayoade_farooq@yahoo.com on 5/25/2017.
 */
public enum TransferExceptions {
    BALANCE("transfer.balance.insufficient"), SAME_ACCOUNT("transfer.account.same"), NO_DEBIT_ACCOUNT("transfer.debit.invalid"), ERROR("transfer.api.failure")
    ,INVALID_ACCOUNT("transfer.account.invalid"), INVALID_BENEFICIARY("transfer.beneficiary.invalid"),LIMIT_EXCEEDED("transfer.limit"),INVALID_AMOUNT("transfer.amount.invalid"),
    FAILED("transfer.send.failure"),NO_BVN("transfer.bvn.invalid"),NOT_ALLOWED("transfer.not.permitted")
    ;


    ;

    private  String name="";

    TransferExceptions(String s) {
        name=s;
    }
    public String toString() {
        return this.name;
    }


}
