
package longbridge.config.audits.listeners;

import longbridge.config.IbankingContext;
import longbridge.config.audits.JdbcAuditConfigRepository;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCPostInsertListener extends EnversPostInsertEventListenerImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BCPostInsertListener(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        String s = event.getEntity().getClass().getCanonicalName();
        logger.debug("{}",event.getState());
        JdbcAuditConfigRepository jdbcAuditConfigRepository = null;
        try {
            jdbcAuditConfigRepository = IbankingContext.getBean(JdbcAuditConfigRepository.class);
            if (jdbcAuditConfigRepository.auditEntity(s)) {
                logger.trace("starting onPostInsert:{}", s);
                super.onPostInsert(event);
            }
        } catch (Exception e) {
            logger.warn("!!! Application context not properly initialized , not auditing !!!");
        }
    }
}

