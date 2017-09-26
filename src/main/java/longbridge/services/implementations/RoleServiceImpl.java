package longbridge.services.implementations;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.services.RoleService;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
@Service
public class RoleServiceImpl implements RoleService {


    private RoleRepo roleRepo;

    private PermissionRepo permissionRepo;

    private MessageSource messageSource;

    private AdminUserRepo adminRepo;

    private RetailUserRepo retailRepo;

    private OperationsUserRepo opRepo;

    private CorporateUserRepo corpRepo;

    private EntityManager entityManager;

    private ModelMapper modelMapper = new ModelMapper();

    private Locale locale = LocaleContextHolder.getLocale();

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public RoleServiceImpl(RoleRepo roleRepo, PermissionRepo permissionRepo, MessageSource messageSource, AdminUserRepo adminRepo, RetailUserRepo retailRepo, OperationsUserRepo opRepo, EntityManager entityManager, CorporateUserRepo corpRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.messageSource = messageSource;
        this.adminRepo = adminRepo;
        this.retailRepo = retailRepo;
        this.opRepo = opRepo;
        this.entityManager = entityManager;
        this.corpRepo = corpRepo;
    }


    @Override
    @Verifiable(operation = "ADD_ROLE", description = "Adding a Role")
    public String addRole(RoleDTO roleDTO) throws InternetBankingException {

        Role role = roleRepo.findByUserTypeAndName(roleDTO.getUserType(),roleDTO.getName());

        if (role != null) {
            throw new DuplicateObjectException(messageSource.getMessage("role.exist", null, locale));

        }

        try {
            role = convertDTOToEntity(roleDTO);
            roleRepo.save(role);
            logger.info("Added role {}", role.toString());
            return messageSource.getMessage("role.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.add.failure", null, locale), e);
        }
    }


    @Override
    public RoleDTO getRole(Long id) {
        Role role = roleRepo.findOne(id);
        return convertEntityToDTO(role);
    }

    @Override
    public Role getTheRole(String roleName) {
        Role role = roleRepo.findFirstByName(roleName);
        return role;
    }

    @Override
    public Role getRoleByUserTypeAndName(UserType userType, String name) {
        return null;
    }

    @Override
    public Iterable<RoleDTO> getRoles() {
        Iterable<Role> roles = roleRepo.findAll();
        return convertRoleEntitiesToDTOs(roles);
    }

    @Override
    @Verifiable(operation = "UPDATE_ROLE", description = "Updating a Role")
    public String updateRole(RoleDTO roleDTO) throws InternetBankingException {

          Role role = roleRepo.findByUserTypeAndName(roleDTO.getUserType(),roleDTO.getName());

        if (role != null && !roleDTO.getId().equals(role.getId())) {
            throw new DuplicateObjectException(messageSource.getMessage("role.exist", null, locale));

        }

        try {
             role = convertDTOToEntity(roleDTO);
            roleRepo.save(role);
            logger.info("Updated role {}", role.toString());
            return messageSource.getMessage("role.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.update.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_ROLE", description = "Deleting a Role")
    public String deleteRole(Long id) throws InternetBankingException {

        Role role = roleRepo.findOne(id);
        Integer users = countUsers(role);
        if (users > 0) {
            throw new InternetBankingException(messageSource.getMessage("role.delete.users.exist", null, locale));
        }
        try {
            roleRepo.delete(role);
            logger.warn("Deleted role with Id {}", id);
            return messageSource.getMessage("role.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.delete.failure", null, locale), e);
        }
    }

    @Verifiable(operation = "ADD_PERMISSION", description = "Adding a Permission")
    public String addPermission(PermissionDTO permissionDTO) throws InternetBankingException {
        try {
            Permission permission = convertDTOToEntity(permissionDTO);
            permissionRepo.save(permission);
            logger.info("Added permission {}", permission.toString());
            return messageSource.getMessage("permission.add.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }

        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("permission.add.failure", null, locale), e);
        }
    }

    @Override
    public PermissionDTO getPermission(Long id) {
        Permission permission = permissionRepo.findOne(id);
        return convertEntityToDTO(permission);
    }

    @Override
    public Iterable<PermissionDTO> getPermissions() {
        Iterable<Permission> permissions = permissionRepo.findAll();
        return convertPermissionEntitiesToDTOs(permissions);
    }

    @Override

    public Page<RoleDTO> getRoles(Pageable pageDetails) {
        Page<Role> page = roleRepo.findAll(pageDetails);
        List<RoleDTO> dtOs = convertRoleEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<RoleDTO> pageImpl = new PageImpl<RoleDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Page<PermissionDTO> getPermissions(Pageable pageDetails) {
        Page<Permission> page = permissionRepo.findAll(pageDetails);
        List<PermissionDTO> dtOs = convertPermissionEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<PermissionDTO> pageImpl = new PageImpl<PermissionDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    @Verifiable(operation = "UPDATE_PERMISSION", description = "Updating a Permission")
    public String updatePermission(PermissionDTO permissionDTO) throws InternetBankingException {
        try {
            Permission permission = permissionRepo.findOne(permissionDTO.getId());
            entityManager.detach(permission);
            permission.setVersion(permissionDTO.getVersion());
            permission.setUserType(permissionDTO.getUserType());
            permission.setCode(permissionDTO.getCode());
            permission.setName(permissionDTO.getName());
            permission.setDescription(permissionDTO.getDescription());
            permissionRepo.save(permission);
            logger.info("Updated permission {}", permission.toString());
            return messageSource.getMessage("permission.update.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("permission.add.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_PERMISSION", description = "Deleting a Permission")
    public String deletePermission(Long id) throws InternetBankingException {
        try {
            Permission permission = permissionRepo.findOne(id);
            permissionRepo.delete(permission);
            logger.warn("Deleted permission with Id {}", id);
            return messageSource.getMessage("permission.delete.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("permission.delete.failure", null, locale), e);
        }
    }

    @Override
    public List<RoleDTO> getRolesByUserType(UserType userType) {
        List<Role> roles = roleRepo.findByUserType(userType);
        return convertRoleEntitiesToDTOs(roles);
    }

    public RoleDTO convertEntityToDTO(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

    private Role convertDTOToEntity(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);
        role.setPermissions(convertPermissionDTOsToEntities(roleDTO.getPermissions()));
        return role;
    }

    private List<RoleDTO> convertRoleEntitiesToDTOs(Iterable<Role> roles) {
        List<RoleDTO> roleDTOList = new ArrayList<>();
        for (Role role : roles) {
            RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);
            roleDTOList.add(roleDTO);
        }
        return roleDTOList;
    }

    private PermissionDTO convertEntityToDTO(Permission permission) {
        return modelMapper.map(permission, PermissionDTO.class);
    }

    private Permission convertDTOToEntity(PermissionDTO permissionDTO) {
        Permission permission;
        if (permissionDTO.getId() == null) {
            permission = modelMapper.map(permissionDTO, Permission.class);
        } else {
            permission = permissionRepo.findOne(permissionDTO.getId());
        }
        return permission;
    }

    private List<PermissionDTO> convertPermissionEntitiesToDTOs(Iterable<Permission> permissions) {
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionDTO permissionDTO = convertEntityToDTO(permission);
            permissionDTOList.add(permissionDTO);
        }
        return permissionDTOList;
    }

    private List<Permission> convertPermissionDTOsToEntities(Iterable<PermissionDTO> permissionDTOs) {
        List<Permission> permissions = new ArrayList<>();
        for (PermissionDTO permissionDTO : permissionDTOs) {
            Permission permission = convertDTOToEntity(permissionDTO);
            permissions.add(permission);
        }
        return permissions;
    }


    @Override
    public Iterable<PermissionDTO> getPermissionsNotInRole(RoleDTO role) {
        // TODO Auto-generated method stub
        Long[] permissionArray = new Long[role.getPermissions().size()];
        int idx = 0;
        for (PermissionDTO perm : role.getPermissions()) {
            permissionArray[idx] = perm.getId();
            idx++;
        }
        //not in NULL check
        if (permissionArray.length == 0)
            permissionArray = new Long[]{-1L};
        Iterable<Permission> permissionsNotInRole = permissionRepo.findByIdNotIn(permissionArray);

        return convertPermissionEntitiesToDTOs(permissionsNotInRole);
    }


    @Override
    public Page<User> getUsers(RoleDTO roledto, Pageable pageDetails) {
        Role role = roleRepo.findOne(roledto.getId());
        Page<User> pageImpl = null;
        switch (role.getUserType()) {
            case ADMIN: {
                Page<AdminUser> users = adminRepo.findByRole(role, pageDetails);
                long elements = users.getTotalElements();
                List<User> userList = (List<User>) (List<?>) users.getContent();
                pageImpl = new PageImpl<User>(userList, pageDetails, elements);
            }

            break;
            case OPERATIONS: {
                Page<OperationsUser> users = opRepo.findByRole(role, pageDetails);
                long elements = users.getTotalElements();
                List<User> userList = (List<User>) (List<?>) users.getContent();
                pageImpl = new PageImpl<User>(userList, pageDetails, elements);
            }
            break;

            case RETAIL: {
                Page<RetailUser> users = retailRepo.findByRole(role, pageDetails);
                long elements = users.getTotalElements();
                List<User> userList = (List<User>) (List<?>) users.getContent();
                pageImpl = new PageImpl<User>(userList, pageDetails, elements);
            }
            break;
            case CORPORATE: {
                Page<CorporateUser> users = corpRepo.findByRole(role, pageDetails);
                long elements = users.getTotalElements();
                List<User> userList = (List<User>) (List<?>) users.getContent();
                pageImpl = new PageImpl<User>(userList, pageDetails, elements);
            }
            break;
        }
        return pageImpl;
    }


    private Integer countUsers(Role role) {
        Integer cnt = 0;
        switch (role.getUserType()) {
            case ADMIN: {
                cnt = adminRepo.countByRole(role);
            }

            break;
            case OPERATIONS: {
                cnt = opRepo.countByRole(role);
            }
            break;

            case RETAIL: {
                cnt = retailRepo.countByRole(role);
            }
            break;
            case CORPORATE: {
                cnt = corpRepo.countByRole(role);
            }
            break;
        }
        return cnt;
    }


    @Override
    public Page<RoleDTO> findRoles(String pattern, Pageable pageDetails) {
        Page<Role> page = roleRepo.findUsingPattern(pattern, pageDetails);
        List<RoleDTO> dtOs = convertRoleEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<RoleDTO> pageImpl = new PageImpl<RoleDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    @Override
    public Page<PermissionDTO> findPermissions(String pattern, Pageable pageDetails) {
        Page<Permission> page = permissionRepo.findUsingPattern(pattern, pageDetails);
        List<PermissionDTO> dtOs = convertPermissionEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<PermissionDTO> pageImpl = new PageImpl<PermissionDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }
}
