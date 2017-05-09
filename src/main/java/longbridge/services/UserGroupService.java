package longbridge.services;

import longbridge.dtos.UserGroupDTO;

/**
 * Created by Fortune on 5/3/2017.
 */


public interface UserGroupService {


    void addGroup(UserGroupDTO userGroupDTO);

    UserGroupDTO getGroup(Long id);

    boolean addOperatorToGroup(Long groupId, String username);
}
