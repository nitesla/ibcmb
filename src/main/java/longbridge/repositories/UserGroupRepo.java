package longbridge.repositories;

import longbridge.models.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface UserGroupRepo extends CommonRepo<UserGroup, Long> {
    UserGroup findByNameIgnoreCase(String groupName);

    @Query("select grp.users from UserGroup grp where grp.id = :groupId")
    Stream<OperationsUser> findInternalMembers(@Param("groupId") Long groupId);

    @Query("select grp.contacts from UserGroup grp where grp.id = :groupId")
    Stream<Contact> findExternalMembers(@Param("groupId") Long groupId);

//  @Query("select count(cr) from CorporateRole cr where :user member of cr.users and cr=:role")
    @Query("select grp.id from UserGroup grp where :opsUser member  of grp.users")
    List<Long> findUserSubscriptions(@Param("opsUser")OperationsUser user);
}
