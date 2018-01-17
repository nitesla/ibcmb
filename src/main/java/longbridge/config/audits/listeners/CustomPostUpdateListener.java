package longbridge.config.audits.listeners;

import longbridge.config.audits.CustomJdbcUtil;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class CustomPostUpdateListener extends EnversPostUpdateEventListenerImpl {

	public CustomPostUpdateListener(EnversService enversService) {
		super(enversService);
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event)
	{
		String s = event.getEntity().getClass().getSimpleName();

		if (CustomJdbcUtil.auditEntity(s)) {
			super.onPostUpdate(event);
		}

	}
}
