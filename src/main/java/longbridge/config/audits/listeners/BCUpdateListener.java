
package longbridge.config.audits.listeners;

import longbridge.config.IbankingContext;
import longbridge.config.audits.JdbcAuditConfigRepository;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCUpdateListener extends EnversPostUpdateEventListenerImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BCUpdateListener(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        String s = event.getEntity().getClass().getCanonicalName();
        JdbcAuditConfigRepository jdbcAuditConfigRepository = null;
        try {
            jdbcAuditConfigRepository = IbankingContext.getBean(JdbcAuditConfigRepository.class);
            if (jdbcAuditConfigRepository.auditEntity(s)) {
                logger.trace("starting onPostUpdate:{}", s);
                super.onPostUpdate(event);
            }
        } catch (Exception e) {
            logger.warn("!!! Application context not properly initialized , not auditing !!!");
        }
    }

}
