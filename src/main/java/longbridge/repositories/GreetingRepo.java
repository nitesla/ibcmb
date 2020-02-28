package longbridge.repositories;

import longbridge.models.Greeting;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GreetingRepo extends CommonRepo<Greeting,Long> {

   Greeting findFirstByEventName(String eventName);

   @Query(value="select * from greeting c where ((c.executed_on <= ?1 and c.expired_on >= ?1 and c.type='GNL') or (c.recurring_date =1 and c.type='GNL'))" +
           " and c.event_name!='BIRTHDAY' and c.event_name!='ACCOUNT_ANNIVERSARY'", nativeQuery = true)
    List<Greeting> findGeneralGreetings(Date today);

   @Query("select a from Greeting a where (:today between a.executedOn and a.expiredOn) and a.type='PNL' and a.userId like %:userId%")
   List<Greeting> findPersonalGreetings(@Param("today") Date date, @Param("userId") long userId);

   List<Greeting> findAll();


}

