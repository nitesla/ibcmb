package longbridge.repositories;

import longbridge.models.CustomerFeedBack;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface CustomerFeedBackRepo extends CommonRepo<CustomerFeedBack,Long>  {

    @Query("select distinct(a.transactionType) from CustomerFeedBack a where a.ratings > 0 and a.transactionType <> null and(a.createdOn BETWEEN :startDate and :endDate) ")
    List<String> findDistinctTranTypes(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select count(a.transactionType),avg(a.ratings) from CustomerFeedBack a where a.transactionType = :tranType and a.userType = :userType and a.ratings > 0 and (a.createdOn BETWEEN :startDate and :endDate)")
    String findAverageForUserType(@Param("tranType") String tranType, @Param("userType") String userType, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select count(a.transactionType),avg(a.ratings) from CustomerFeedBack a where a.transactionType = :tranType and a.ratings > 0 and (a.createdOn BETWEEN :startDate and :endDate)")
    String findAverageForBothTypes(@Param("tranType") String tranType, @Param("startDate") Date startDate, @Param("endDate") Date endDate);


}
