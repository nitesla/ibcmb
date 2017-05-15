package longbridge.services.implementations;

import longbridge.dtos.UserGroupDTO;
import longbridge.models.UserGroup;
import longbridge.repositories.UserGroupRepo;
import longbridge.services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Fortune on 5/3/2017.
 */

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    UserGroupRepo userGroupRepo;

    @Override
    public void addGroup(UserGroupDTO userGroupDTO) {

    }

    @Override
    public UserGroupDTO getGroup(Long id) {
        return null;
    }

    @Override
    public boolean addOperatorToGroup(Long groupId, String username) {
        boolean ok = false;
        UserGroup group = userGroupRepo.findOne(groupId);
        return true;
    }
}
