package longbridge.services;

import longbridge.dtos.ContactDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Fortune on 5/3/2017.
 */


public interface UserGroupService {


    String addGroup(UserGroupDTO userGroupDTO) throws InternetBankingException;

    String updateGroup(UserGroupDTO userGroupDTO) throws InternetBankingException;

    String deleteGroup(Long id) throws InternetBankingException;

    List<UserGroupDTO> getGroups();

    Page<UserGroupDTO> getGroups(Pageable pageDetails);

    UserGroupDTO getGroup(Long id);

    List<ContactDTO> getContacts(Long groupId);

}
