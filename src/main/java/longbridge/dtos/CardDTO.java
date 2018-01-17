package longbridge.dtos;



/**
 * Created by Fortune on 4/5/2017.
 */
public class CardDTO {

    private Long id;
    private String cardReference;
    private String cardNumber;
    private String cardName;
    private String expiryDate;
    private String cardType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardReference() {
        return cardReference;
    }

    public void setCardReference(String cardReference) {
        this.cardReference = cardReference;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardReference='" + cardReference + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardName='" + cardName + '\'' +
                ", cardType=" + cardType +
                '}';
    }
}
