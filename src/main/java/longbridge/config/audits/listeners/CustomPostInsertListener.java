package longbridge.config.audits.listeners;


import longbridge.config.SpringContext;
import longbridge.config.audits.CustomJdbcUtil;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
//@Component
public class CustomPostInsertListener extends EnversPostInsertEventListenerImpl {


    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    public CustomPostInsertListener(EnversService enversService) {
        super(enversService);

    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        String s = event.getEntity().getClass().getSimpleName();

        CustomJdbcUtil customJdbcUtil = null;
        try {
            ApplicationContext applicationContext = SpringContext.getApplicationContext();
            customJdbcUtil = applicationContext.getBean(CustomJdbcUtil.class);
            if (customJdbcUtil.auditEntity(s)) {
                super.onPostInsert(event);
            }
        } catch (Exception e) {
            logger.warn("!!! Application context not properly initialized , not auditing !!!", e);
        }
    }

}
