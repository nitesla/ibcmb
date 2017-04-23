package longbridge.models.audits.listeners;

import longbridge.models.audits.CustomJdbcUtil;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */

public class CustomPostInsertListener extends EnversPostInsertEventListenerImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9002213873196744995L;

	/**
	 * 
	 */

	public CustomPostInsertListener(EnversService enversService) {
		super(enversService);
	}

	@Override

	public void onPostInsert(PostInsertEvent event) {
		String s = event.getEntity().getClass().getSimpleName();
		System.out.println(s + "ok na");
		if (CustomJdbcUtil.auditEntity(s)) {
			System.out.println("Meaning i can control it?");
			super.onPostInsert(event);
		} 
	}

}
