package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by Showboy on 07/06/2017.
 */
public class SecQuestionDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty
    private String securityQuestion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
}
