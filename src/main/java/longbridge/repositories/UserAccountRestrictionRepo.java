package longbridge.repositories;

import longbridge.models.UserAccountRestriction;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
@Transactional
public interface UserAccountRestrictionRepo extends CommonRepo<UserAccountRestriction,Long> {

    List<UserAccountRestriction> findByCorporateUserId(Long userId);

    UserAccountRestriction findByCorporateUserIdAndAccountId(Long userId, Long accountId);
}
