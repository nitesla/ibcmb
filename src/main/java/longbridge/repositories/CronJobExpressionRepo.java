package longbridge.repositories;

import longbridge.models.CronJobExpression;
import org.springframework.stereotype.Repository;

/**
 * Created by Longbridge on 6/15/2017.
 */
@Repository
public interface CronJobExpressionRepo extends CommonRepo<CronJobExpression,Long> {
    CronJobExpression findLastByFlagAndCategory(String flag,String category);
    CronJobExpression findLastByFlag(String flag);
}
