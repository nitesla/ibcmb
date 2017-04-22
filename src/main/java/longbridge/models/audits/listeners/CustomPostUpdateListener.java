package longbridge.models.audits.listeners;

import longbridge.models.audits.CustomJdbcUtil;
import longbridge.repositories.AuditConfigRepo;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class CustomPostUpdateListener extends EnversPostUpdateEventListenerImpl {


    public CustomPostUpdateListener(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        String  s=event.getEntity().getClass().getSimpleName();

        if (CustomJdbcUtil.auditEntity(s)){
            System.out.println("Meaning i can control it?");
          System.out.println(event.getEntity().getClass().getSimpleName());
            super.onPostUpdate(event);
        }

    }
}
