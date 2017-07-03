package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Created by Showboy on 24/06/2017.
 */
@Entity
@Where(clause ="del_Flag='N'")
public class Faqs extends AbstractEntity {

    private String question;

    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Faqs{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
