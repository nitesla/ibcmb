package longbridge.models.audits.listeners;




import longbridge.models.audits.CustomRevisionEntity;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.security.Principal;
/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class EntityRevisionListener/*<T extends RevisionsEntity>*/  implements EntityTrackingRevisionListener {


    private Principal principal;




    public String getUser(){

        //would use spring security later to get Principal.getName() or any other way

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return username;
                //principal.getName();



    }

    @Override
    public void newRevision(Object o) {
      //  System.out.println("New revision is created: " + o);


        CustomRevisionEntity revision = (CustomRevisionEntity)o;



            revision.setLastChangedBy(getUser());

    }

    @Override
    public void entityChanged(Class aClass, String s, Serializable serializable, RevisionType revisionType, Object o) {
        String type = aClass.getName();
        ((CustomRevisionEntity)o).addModifiedEntityType(type);
    }
}
