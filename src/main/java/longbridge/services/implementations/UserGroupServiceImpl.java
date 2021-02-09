package longbridge.services.implementations;

import longbridge.dtos.ContactDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Contact;
import longbridge.models.OperationsUser;
import longbridge.models.UserGroup;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.UserGroupRepo;
import longbridge.services.UserGroupService;
import longbridge.utils.Verifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 5/3/2017.
 */

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupRepo userGroupRepo;


    @Autowired
    private OperationsUserRepo operationsUserRepo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();


    @Override
    @Verifiable(operation="ADD_USER_GRP",description="Adding a Group")
    public String addGroup(UserGroupDTO userGroupDTO)  {
        try {
            UserGroup userGroup = convertDTOToEntity(userGroupDTO);
            userGroupRepo.save(userGroup);
            return messageSource.getMessage("group.add.success",null,locale);
        }

        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }

        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("group.add.failure",null,locale),e);
        }
    }

    @Verifiable(operation="UPDATE_USER_GRP",description="Updating a Group")
    public String updateGroup(UserGroupDTO userGroupDTO) {
        try {
            UserGroup userGroup = convertDTOToEntity(userGroupDTO);
            userGroup.setId(userGroupDTO.getId());
            userGroup.setVersion(userGroupDTO.getVersion());
            userGroupRepo.save(userGroup);
            return messageSource.getMessage("group.update.success",null,locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }


        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("group.update.failure",null,locale),e);
        }
    }

    @Verifiable(operation="DELETE_USER_GRP",description="Deleting a Group")
    public String deleteGroup(Long id) {
        try {
           UserGroup group = userGroupRepo.findById(id).get();
           UserGroup userGroup = new UserGroup();
           userGroup.setId(group.getId());
           userGroup.setVersion(group.getVersion());
           userGroup.setName(group.getName());
           userGroup.setContacts(group.getContacts());
           userGroup.setUsers(group.getUsers());
           userGroupRepo.delete(userGroup);
            return messageSource.getMessage("group.delete.success",null,locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("group.delete.failure",null,locale),e);
        }
    }

    @Override
    public Page<UserGroupDTO> getGroups(Pageable pageDetails) {
        Page<UserGroup> page = userGroupRepo.findAll(pageDetails);
        List<UserGroupDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }


    @Override
    public UserGroupDTO getGroup(Long id) {
        UserGroup userGroup = userGroupRepo.findById(id).get();
        return convertEntityToDTO(userGroup);
    }

    @Override
    public List<UserGroupDTO> getGroups() {
        List<UserGroup> userGroups = userGroupRepo.findAll();
        return convertEntitiesToDTOs(userGroups);
    }



    private List<UserGroupDTO> convertEntitiesToDTOs(Iterable<UserGroup> userGroups){
        List<UserGroupDTO> userGroupDTOs = new ArrayList<>();
        for(UserGroup userGroup: userGroups){
            userGroupDTOs.add(convertEntityToDTO(userGroup));
        }
        return userGroupDTOs;
    }


    private UserGroup convertDTOToEntity(UserGroupDTO userGroupDTO){
        List<OperationsUser> operationsUserList = new ArrayList<>();
        List<Contact> contactList = new ArrayList<>();

        List<OperationsUserDTO> operationsUserDTOs = userGroupDTO.getUsers();
        List<ContactDTO> contactDTOs = userGroupDTO.getContacts();
        UserGroup userGroup = new UserGroup();

        Contact contact;
        OperationsUser operationsUser;

        for(OperationsUserDTO operationsUserDTO: operationsUserDTOs){
            operationsUser = operationsUserRepo.findById(operationsUserDTO.getId()).get();
            operationsUserList.add(operationsUser);
        }

        for(ContactDTO contactDTO: contactDTOs){
            contact = new Contact();
            contact.setFirstName(contactDTO.getFirstName());
            contact.setLastName(contactDTO.getLastName());
            contact.setEmail(contactDTO.getEmail());
            contactList.add(contact);

        }
        userGroup.setName(userGroupDTO.getName());
        userGroup.setDateCreated(new Date());
        userGroup.setUsers(operationsUserList);
        userGroup.setContacts(contactList);
        return userGroup;
    }


    private UserGroupDTO convertEntityToDTO(UserGroup userGroup){
        UserGroupDTO userGroupDTO = new UserGroupDTO();
        userGroupDTO.setId(userGroup.getId());
        userGroupDTO.setVersion(userGroup.getVersion());
        userGroupDTO.setName(userGroup.getName());
        return userGroupDTO;
    }
    @Override
    public List<ContactDTO> getContacts(Long groupId){
        UserGroup userGroup = userGroupRepo.findById(groupId).get();
        List<OperationsUser> internalUsers = userGroup.getUsers();
        List<Contact> externalUsers= userGroup.getContacts();
        List<ContactDTO> allUsers = new ArrayList<>();
        for(OperationsUser opsUser: internalUsers){
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setFirstName(opsUser.getFirstName());
            contactDTO.setLastName(opsUser.getLastName());
            contactDTO.setEmail(opsUser.getEmail());
            contactDTO.setDt_RowId(opsUser.getId());
            contactDTO.setExternal(false);
            allUsers.add(contactDTO);
        }
        for(Contact extUser: externalUsers){
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setFirstName(extUser.getFirstName());
            contactDTO.setLastName(extUser.getLastName());
            contactDTO.setEmail(extUser.getEmail());
            contactDTO.setDt_RowId(extUser.getId());
            contactDTO.setExternal(true);
            allUsers.add(contactDTO);
        }
        return allUsers;
    }

    @Override
    public List<ContactDTO> getContacts(UserGroup userGroup){
        List<OperationsUser> internalUsers = userGroup.getUsers();
        List<Contact> externalUsers= userGroup.getContacts();
        List<ContactDTO> allUsers = new ArrayList<>();
        for(OperationsUser opsUser: internalUsers){
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setFirstName(opsUser.getFirstName());
            contactDTO.setLastName(opsUser.getLastName());
            contactDTO.setEmail(opsUser.getEmail());
            contactDTO.setDt_RowId(opsUser.getId());
            contactDTO.setExternal(false);
            allUsers.add(contactDTO);
        }
        for(Contact extUser: externalUsers){
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setFirstName(extUser.getFirstName());
            contactDTO.setLastName(extUser.getLastName());
            contactDTO.setEmail(extUser.getEmail());
            contactDTO.setDt_RowId(extUser.getId());
            contactDTO.setExternal(true);
            allUsers.add(contactDTO);
        }
        return allUsers;
    }
}
