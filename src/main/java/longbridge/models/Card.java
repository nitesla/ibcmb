package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Card extends AbstractEntity{

    private String cardReference;
    private String cardName;
    private Long cardType;

}
