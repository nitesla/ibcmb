package longbridge.dtos;

 import java.io.Serializable;

/**
 * Created by chiomarose on 21/06/2017.
 */

    public class PendingDTO implements Serializable {

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


