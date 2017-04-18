package longbridge.models.audits.listeners;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */
public class CustomPostInsertListener extends EnversPostInsertEventListenerImpl {



    public CustomPostInsertListener(EnversService enversService) {

            super(enversService);


    }

    @Override
    public void onPostInsert(PostInsertEvent event) {

        if (event.getEntity().getClass().getSimpleName().equalsIgnoreCase("Employees")){
            System.out.println("Meaning i can control it?");
            System.out.println(event.getEntity().getClass().getSimpleName());
           // super.onPostInsert(event);
        }else{
            System.out.println("BABA NA BEANS O");
           // super.onPostInsert(event);
        }

    }



}
