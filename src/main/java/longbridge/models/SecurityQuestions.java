package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Created by Showboy on 07/06/2017.
 */
@Entity
@Where(clause ="del_Flag='N'")
public class SecurityQuestions extends AbstractEntity {

    private String securityQuestion;


    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    @Override
    public String toString() {
        return "SecurityQuestions{" +
                "securityQuestion='" + securityQuestion + '\'' +
                '}';
    }
}
