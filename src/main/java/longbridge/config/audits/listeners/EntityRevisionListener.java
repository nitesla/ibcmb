package longbridge.config.audits.listeners;

import longbridge.config.IbankingContext;
import longbridge.config.audits.Revision;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.trace.TraceStore;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class EntityRevisionListener implements EntityTrackingRevisionListener {


    private String getUser() {
        // would use spring security later to get Principal.getName() or any
        // other way
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
            if (principal.getUser().getUserType() == UserType.CORPORATE) {
                CorporateUser cuser = (CorporateUser) principal.getUser();
                return cuser.getCorporate().getName() + "::" + cuser.getUserName();
            }
            return auth.getName();
        }

        return "Unknown";
    }

    private UserType getUserType() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
            return principal.getUser().getUserType();
        }
        return null;
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

        Revision revision = (Revision) o;

        revision.setLastChangedBy(getTraceId());
        revision.setIpAddress(getIP());
        revision.setType(getUserType());
        revision.setTrace(getTrace());
        revision.setApprovedBy(getUser());


    }

    @Override
    public void entityChanged(Class aClass, String s, Serializable serializable, RevisionType revisionType, Object o) {
        String type = aClass.getName();
        ((Revision) o).addModifiedEntityType(type);
    }

    private String getTrace() {
        TraceStore store = IbankingContext.getBean(TraceStore.class);
        return store.getTrace();
    }

    private String getTraceId() {
        TraceStore store = IbankingContext.getBean(TraceStore.class);
        if (store.getId() == null)
            return getUser();
        else return store.getId();
    }
}
