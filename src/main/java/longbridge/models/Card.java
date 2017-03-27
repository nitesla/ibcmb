package longbridge.models;

import javax.persistence.*;

/**
 * Created by Showboy on 27/03/2017.
 */
@Entity
public class Card {

    @Id
    private Long Id;
    private String cardReference;
    private String cardName;
    private Long cardType;

}
