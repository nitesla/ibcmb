package longbridge.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 6/19/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRate
{
    private String currencyCode;
    private String amountValue;

    public ExchangeRate(String currencyCode, String amountValue) {
        this.currencyCode = currencyCode;
        this.amountValue = amountValue;



    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(String amountValue) {
        this.amountValue = amountValue;
    }
}
