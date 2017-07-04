package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by chiomarose on 21/06/2017.
 */
public class PendingVerification {

    @JsonProperty("DT_RowId")
    Long id;
    String entityName;
    int numPending;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public int getNumPending() {
        return numPending;
    }

    public void setNumPending(int numPending) {
        this.numPending = numPending;
    }
}

