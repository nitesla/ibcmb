package longbridge.dtos;

 import java.io.Serializable;

public class AccountDetailsDTO implements Serializable {
    private String instrumentType;
    private String collectionType;
    private String currencyCode;

    public AccountDetailsDTO() {
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "AccountDetailsDTO{" +
                "instrumentType='" + instrumentType + '\'' +
                ", collectionType='" + collectionType + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
