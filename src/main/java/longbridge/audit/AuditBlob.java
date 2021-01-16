package longbridge.audit;

public class AuditBlob {

    private String name;
    private Object now;
    private Object before;

    private boolean isModified;

    public Object getNow() {
        return now;
    }

    public void setNow(Object now) {
        this.now = now;
    }

    public Object getBefore() {
        return before;
    }

    public void setBefore(Object before) {
        this.before = before;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public boolean isModified() {
        if(now == null && before == null)
            return false;
        if(now != null)
            return !now.equals(before);
        return true;
    }


    public static final class AuditBlobBuilder {
        private AuditBlob auditBlob;

        private AuditBlobBuilder() {
            auditBlob = new AuditBlob();
        }

        public static AuditBlobBuilder anAuditBlob() {
            return new AuditBlobBuilder();
        }

        public AuditBlobBuilder withName(String name) {
            auditBlob.setName(name);
            return this;
        }

        public AuditBlobBuilder withNow(Object now) {
            auditBlob.setNow(now);
            return this;
        }

        public AuditBlobBuilder withBefore(Object before) {
            auditBlob.setBefore(before);
            return this;
        }

        public AuditBlob build() {
            return auditBlob;
        }
    }
}
