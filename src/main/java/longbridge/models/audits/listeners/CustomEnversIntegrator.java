package longbridge.models.audits.listeners;

import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.boot.internal.EnversIntegrator;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversListenerDuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */

public class CustomEnversIntegrator extends EnversIntegrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
        listenerRegistry.addDuplicationStrategy( EnversListenerDuplicationStrategy.INSTANCE );
        
        EnversService enversService = serviceRegistry.getService(EnversService.class);
        if (!enversService.isInitialized()) {
            throw new HibernateException("Expecting EnversService to have been initialized prior to call to EnversIntegrator#integrate");
        }
        if(enversService.getEntitiesConfigurations().hasAuditedEntities()) {
           listenerRegistry.appendListeners( EventType.POST_UPDATE, new PostUpdateEventListener[]{new CustomPostUpdateListener(enversService)});
			listenerRegistry.appendListeners(EventType.POST_INSERT,
					new PostInsertEventListener[] { new CustomPostInsertListener(enversService)});

           // listenerRegistry.appendListeners( EventType.POST_DELETE, new PostDeleteListenerLog( enversService ) );
        }
    }





}
