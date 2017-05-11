package longbridge.services.implementations;

import longbridge.dtos.ContactDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Contact;
import longbridge.models.OperationsUser;
import longbridge.models.UserGroup;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.UserGroupRepo;
import longbridge.services.UserGroupService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

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
    UserGroupRepo userGroupRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    OperationsUserRepo operationsUserRepo;

    @Autowired
    MessageSource messageSource;

    Locale locale = LocaleContextHolder.getLocale();

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String addGroup(UserGroupDTO userGroupDTO) throws InternetBankingException {
        try {
            UserGroup userGroup = convertDTOToEntity(userGroupDTO);
            userGroupRepo.save(userGroup);
            return messageSource.getMessage("group.add.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("group.add.failure",null,locale),e);
        }
    }

    public String updateGroup(UserGroupDTO userGroupDTO) throws InternetBankingException{
        try {
            UserGroup userGroup = convertDTOToEntity(userGroupDTO);
            userGroupRepo.save(userGroup);
            return messageSource.getMessage("group.update.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("group.update.failure",null,locale),e);
        }
    }

    public String deleteGroup(Long id) throws InternetBankingException{
        try {
            userGroupRepo.delete(id);
            return messageSource.getMessage("group.delete.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("group.delete.failure",null,locale),e);
        }
    }


    @Override
    public UserGroupDTO getGroup(Long id) {
        UserGroup userGroup = userGroupRepo.findOne(id);
        return modelMapper.map(userGroup,UserGroupDTO.class);
    }

    @Override
    public boolean addOperatorToGroup(Long groupId, String username) {
        boolean ok = false;
        UserGroup group = userGroupRepo.findOne(groupId);
        return true;
    }

    private UserGroup convertDTOToEntity(UserGroupDTO userGroupDTO){
        List<OperationsUser> operationsUserList = new ArrayList<OperationsUser>();
        List<Contact> contactList =  new ArrayList<Contact>();

        List<OperationsUserDTO> operationsUserDTOs = userGroupDTO.getUsers();
        List<ContactDTO> contactDTOs = userGroupDTO.getContacts();
        UserGroup userGroup = new UserGroup();

        Contact contact;
        OperationsUser operationsUser;

        for(OperationsUserDTO operationsUserDTO: operationsUserDTOs){
            operationsUser = operationsUserRepo.findOne(operationsUserDTO.getId());
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
        userGroupRepo.save(userGroup);
        return userGroup;
    }

    private UserGroupDTO convertEntityToDT(UserGroup userGroup){
        return modelMapper.map(userGroup,UserGroupDTO.class);
    }
}
