package longbridge.models.audits.listeners;

import longbridge.models.audits.CustomJdbcUtil;
import longbridge.repositories.AuditConfigRepo;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by ayoade_farooq@yahoo.com on 4/8/2017.
 */


public class CustomPostInsertListener extends EnversPostInsertEventListenerImpl {
//    @Autowired
//private AuditConfigImpl auditConfig;

//    @Autowired
//    private AuditConfigRepo configRepo;




    public CustomPostInsertListener(EnversService enversService) {

     super(enversService);

    }




    @Override

    public void onPostInsert(PostInsertEvent event) {
   String  s=event.getEntity().getClass().getSimpleName();
        System.out.println(s + "ok na");








        if (CustomJdbcUtil.auditEntity(s)){
            System.out.println("Meaning i can control it?");

           super.onPostInsert(event);
        }else{

        }

    }





}
