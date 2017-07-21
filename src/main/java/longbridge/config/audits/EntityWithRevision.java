package longbridge.config.audits;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class EntityWithRevision <T> {

    private CustomRevisionEntity revision;

    private T entity;

    public EntityWithRevision(CustomRevisionEntity revision, T entity)
    {
        this.revision = revision;
        this.entity = entity;
    }

    public CustomRevisionEntity getRevision() {
        return revision;
    }

    public T getEntity() {
        return entity;
    }
}
