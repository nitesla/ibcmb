package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by Showboy on 24/06/2017.
 */
public class NotificationsDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;

    @NotEmpty(message = "message")
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
