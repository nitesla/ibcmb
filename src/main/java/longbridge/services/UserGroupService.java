package longbridge.services;

import longbridge.dtos.ContactDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.models.OperationsUser;
import longbridge.models.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by Fortune on 5/3/2017.
 */


public interface UserGroupService {

    @PreAuthorize("hasAuthority('ADD_USER_GRP')")
    String addGroup(UserGroupDTO userGroupDTO) ;

    @PreAuthorize("hasAuthority('UPDATE_USER_GRP')")
    String updateGroup(UserGroupDTO userGroupDTO) ;

    @PreAuthorize("hasAuthority('DELETE_USER_GRP')")
    String deleteGroup(Long id) ;

    @PreAuthorize("hasAuthority('GET_USER_GRP')")
    List<UserGroupDTO> getGroups();

    @PreAuthorize("hasAuthority('GET_USER_GRP')")
    Page<UserGroupDTO> getGroups(Pageable pageDetails);

//    @PreAuthorize("hasAuthority('GET_USER_GRP')")
    UserGroupDTO getGroup(Long id);

//    @PreAuthorize("hasAuthority('GET_USER_GRP')")
    List<ContactDTO> getContacts(Long groupId);

    List<ContactDTO> getContacts(UserGroup userGroup);

    List<Long> getGroups(OperationsUser user);
}
