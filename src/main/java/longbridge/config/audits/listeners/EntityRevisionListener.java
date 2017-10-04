package longbridge.config.audits.listeners;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.security.userdetails.CustomUserPrincipal;

import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class EntityRevisionListener/*<T extends RevisionsEntity>*/ implements EntityTrackingRevisionListener {


    private String getUser() {
        // would use spring security later to get Principal.getName() or any
        // other way
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
            return auth.getName();
        return "Unknown";
    }

    private String getIP() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
            return principal.getIpAddress();
        }
        return "Unknown";
    }


    @Override
    public void newRevision(Object o) {
        // System.out.println("New revision is created: " + o);

        CustomRevisionEntity revision = (CustomRevisionEntity) o;

        revision.setLastChangedBy(getUser());
        revision.setIpAddress(getIP());

    }

    @Override
    public void entityChanged(Class aClass, String s, Serializable serializable, RevisionType revisionType, Object o) {
        String type = aClass.getName();
        ((CustomRevisionEntity) o).addModifiedEntityType(type);
        ((CustomRevisionEntity) o).setIpAddress(getIP());
        ((CustomRevisionEntity) o).setLastChangedBy(getUser());


    }
}
