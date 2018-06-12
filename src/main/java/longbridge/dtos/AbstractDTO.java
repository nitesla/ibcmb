package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Fortune on 6/12/2018.
 */
public abstract class AbstractDTO {

    @JsonProperty("DT_RowId")
    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
