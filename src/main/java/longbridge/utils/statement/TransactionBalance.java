package longbridge.utils.statement;


/**
 * Created by ayoade_farooq@yahoo.com on 6/15/2017.
 */

public class TransactionBalance {
  private String amountValue;
  private String currencyCode;


  public String getAmountValue() {
    return amountValue;
  }

  public void setAmountValue(String amountValue) {
    this.amountValue = amountValue;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  @Override
  public String toString() {
    return "TransactionBalance{" +
            "amountValue='" + amountValue + '\'' +
            ", currencyCode='" + currencyCode + '\'' +
            '}';
  }
}
