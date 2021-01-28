package longbridge.config.audits.listeners;

import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.boot.internal.EnversIntegrator;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversListenerDuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class CustomEnversIntegrator extends EnversIntegrator {


    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        listenerRegistry.addDuplicationStrategy(EnversListenerDuplicationStrategy.INSTANCE);

        EnversService enversService = serviceRegistry.getService(EnversService.class);
        if (enversService.isEnabled()) {
            if (!enversService.isInitialized()) {
                throw new HibernateException("Expecting Envers Service to have been initialized prior to call to EnversIntegrator#integrate");
            }
            if (enversService.getEntitiesConfigurations().hasAuditedEntities()) {
                listenerRegistry.appendListeners(EventType.POST_UPDATE, new BCUpdateListener(enversService));
                listenerRegistry.appendListeners(EventType.POST_INSERT,
                        new BCPostInsertListener(enversService));
            }
        }
    }
}
