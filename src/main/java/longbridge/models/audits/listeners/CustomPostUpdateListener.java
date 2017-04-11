package longbridge.models.audits.listeners;

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
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity().getClass().getSimpleName().equalsIgnoreCase("Employees")){
            System.out.println("Meaning i can control it?");
           // System.out.println(event.getEntity().getClass().getSimpleName());

        }else{
            System.out.println("BABA NA BEANS O");
            super.onPostUpdate(event);
        }

    }
}
