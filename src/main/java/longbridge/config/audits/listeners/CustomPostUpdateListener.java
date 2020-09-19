package longbridge.config.audits.listeners;

import longbridge.config.SpringContext;
import longbridge.config.audits.CustomJdbcUtil;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


public class CustomPostUpdateListener extends EnversPostUpdateEventListenerImpl {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    public CustomPostUpdateListener(EnversService enversService) {
        super(enversService);

    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        String s = event.getEntity().getClass().getSimpleName();

        CustomJdbcUtil customJdbcUtil = null;
        try {
            ApplicationContext applicationContext = SpringContext.getApplicationContext();
            customJdbcUtil = applicationContext.getBean(CustomJdbcUtil.class);
            if (customJdbcUtil.auditEntity(s)) {
                super.onPostUpdate(event);
            }
        } catch (Exception e) {
            logger.warn("!!! Application context not properly initialized , not auditing !!!", e);
        }
    }


}
