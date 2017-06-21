package longbridge.dtos;

/**
 * Created by Fortune on 6/21/2017.
 */
public class PendingVerification {

    String entityName;
    int numPending;

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
