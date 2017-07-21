package longbridge.dtos;

/**
 * Created by chiomarose on 03/07/2017.
 */
public class AuditConfigDTO {


    private long id;

    private String entityName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    private String enabled;
}
