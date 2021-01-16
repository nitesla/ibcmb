package longbridge.config.audits;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class ModifiedType implements Serializable {
    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    @JsonManagedReference
    private Revision revision;
    private String entity;

    public ModifiedType() {
    }


    public ModifiedType(Revision revision, String entity) {
        this.revision = revision;
        this.entity = entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

}
