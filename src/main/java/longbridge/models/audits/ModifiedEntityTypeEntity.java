package longbridge.models.audits;

import javax.persistence.*;

/**
 * Created by ayoade_farooq@yahoo.com on 4/7/2017.
 */
@Entity
public class ModifiedEntityTypeEntity {



    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private CustomRevisionEntity revision;

    private String entityClassName;


    public ModifiedEntityTypeEntity(CustomRevisionEntity revision, String entityClassName) {
        this.revision = revision;
        this.entityClassName = entityClassName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomRevisionEntity getRevision() {
        return revision;
    }

    public void setRevision(CustomRevisionEntity revision) {
        this.revision = revision;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }
}
