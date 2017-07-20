package longbridge.config.audits.listeners;

//import longbridge.config.audits.CustomJdbcUtil;
import longbridge.config.audits.CustomJdbcUtil;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;
import org.springframework.stereotype.Component;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
//@Component
public class CustomPostInsertListener extends EnversPostInsertEventListenerImpl {

	/**
	 *
	 */
	private static final long serialVersionUID = 9002213873196744995L;

	public CustomPostInsertListener(EnversService enversService)
	{
		super(enversService);
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		String s = event.getEntity().getClass().getSimpleName();
		if (CustomJdbcUtil.auditEntity(s))
		{
			super.onPostInsert(event);
		}
	}

}
