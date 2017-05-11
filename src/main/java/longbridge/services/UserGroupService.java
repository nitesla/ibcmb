package longbridge.services;

import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;

/**
 * Created by Fortune on 5/3/2017.
 */


public interface UserGroupService {


    String addGroup(UserGroupDTO userGroupDTO) throws InternetBankingException;

    String updateGroup(UserGroupDTO userGroupDTO) throws InternetBankingException;

    String deleteGroup(Long id) throws InternetBankingException;



    UserGroupDTO getGroup(Long id);

    boolean addOperatorToGroup(Long groupId, String username);
}
