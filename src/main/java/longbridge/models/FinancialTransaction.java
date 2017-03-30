package longbridge.models;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Wunmi on 3/28/2017.
 */


public class FinancialTransaction{
    public static enum TranType {DEBIT, CREDIT}
    private String transactionParticulars;
    private String transactionRemarks;
    private BigDecimal amount;
    private BigDecimal currentBalance;
    private TranType tranType;

    private LocalDateTime valueDate;
    private LocalDateTime postDate;


}
