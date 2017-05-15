package longbridge.services.implementations;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.services.RoleService;
import longbridge.services.VerificationService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RoleRepo roleRepo;

    private VerificationService verificationService;

    private PermissionRepo permissionRepo;

    private MessageSource messageSource;

    private ModelMapper modelMapper = new ModelMapper();

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public RoleServiceImpl(RoleRepo roleRepo, VerificationService verificationService, PermissionRepo permissionRepo, MessageSource messageSource) {
        this.roleRepo = roleRepo;
        this.verificationService = verificationService;
        this.permissionRepo = permissionRepo;
        this.messageSource = messageSource;
    }


// @Override
    // public Role deserialize(String data) throws IOException {
    // Role role = new Role();
    // ObjectMapper mapper = new ObjectMapper();
    // JsonNode node = mapper.readTree(data);
    // ObjectNode objectNode = nodeFactory.objectNode();
    // role.setId(objectNode.get("id").asLong());
    // role.setVersion(objectNode.get("version").asInt());
    // role.setName(objectNode.get("name").asText());
    // role.setEmail(objectNode.get("email").asText());
    // role.setUserType(UserType.valueOf(objectNode.get("userType").asText()));
    //
    // //deserialize the listentity
    // List<String> ids = objectNode.findValuesAsText("permissions");
    // Collection<Permission> permissionCollection = new
    // LinkedList<Permission>();
    // for(String id: ids){
    // Permission permission = new Permission();
    // permission.setId();
    // }
    //// ArrayNode arrayNode = objectNode.putArray("permissions");
    // objectNode.put("permissions",serializeEntityList(role.getPermissions(),
    // arrayNode));
    // return objectNode.toString();
    // }
    //
    //// @Override
    // public String serialize(Role role) throws JsonProcessingException {
    // ObjectNode objectNode = nodeFactory.objectNode();
    // objectNode.put("id", role.getId());
    // objectNode.put("email", role.getEmail());
    // objectNode.put("name", role.getName());
    // objectNode.put("userType", role.getUserType().toString());
    //// ArrayNode arrayNode = objectNode.putArray("permissions");
    // objectNode.put("permissions",serializeEntityList(role.getPermissions(),
    // arrayNode));
    // return objectNode.toString();
    // }

//
//	@Override
//	public void updateRole(Role role) {
//		roleRepo.save(role);
//	}
//
//	@Override
//	public void deleteRole(Role role) {
//		role.setDelFlag("Y");
//		roleRepo.save(role);
//	}
//
//	@Override
//	public void addPermission(Permission permission) {
//		permissionRepo.save(permission);
//	}
//
//	@Override
//	public void updatePermission(Permission permission) {
//		permissionRepo.save(permission);
//	}
//
//	@Override
//	public void deletePermission(Permission permission) {
//		permission.setDelFlag("Y");
//		permissionRepo.save(permission);
//	}

    @Override
    public String addRole(RoleDTO roleDTO) throws InternetBankingException {
        try {
            Role role = convertDTOToEntity(roleDTO);
            roleRepo.save(role);
            logger.info("Added role {}", role.toString());
            return messageSource.getMessage("role.add.success", null, locale);
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
    public Role getTheRole(Long id) {
        Role role = roleRepo.findOne(id);
        return role;
    }

    @Override
    public Iterable<RoleDTO> getRoles() {
        Iterable<Role> roles = roleRepo.findAll();
        return convertRoleEntitiesToDTOs(roles);
    }

    @Override
    public String updateRole(RoleDTO roleDTO) throws InternetBankingException {
        try {
            Role role = convertDTOToEntity(roleDTO);
            roleRepo.save(role);
            logger.info("Updated role {}", role.toString());
            return messageSource.getMessage("role.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.update.failure", null, locale), e);
        }
    }

    @Override
    public String deleteRole(Long id) throws InternetBankingException {
        try {
            roleRepo.delete(id);
            logger.warn("Deleted role with Id {}", id);
            return messageSource.getMessage("role.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.delete.failure", null, locale), e);
        }
    }

    public String addPermission(PermissionDTO permissionDTO) throws InternetBankingException {
        try {
            Permission permission = convertDTOToEntity(permissionDTO);
            permissionRepo.save(permission);
            logger.info("Added permission {}", permission.toString());
            return messageSource.getMessage("permission.add.success", null, locale);
        } catch (Exception e) {
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
    public String updatePermission(PermissionDTO permissionDTO) throws InternetBankingException {
        try {
            Permission permission = convertDTOToEntity(permissionDTO);
            permissionRepo.save(permission);
            logger.info("Updated permission {}", permission.toString());
            return messageSource.getMessage("permission.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("permission.add.failure", null, locale), e);
        }
    }

    @Override
    public String deletePermission(Long id) throws InternetBankingException {
        try {
            permissionRepo.delete(id);
            logger.warn("Deleted permission with Id {}", id);
            return messageSource.getMessage("permission.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("permission.delete.failure",null,locale),e);
        }
    }

    public RoleDTO convertEntityToDTO(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

    private Role convertDTOToEntity(RoleDTO roleDTO) {
        return modelMapper.map(roleDTO, Role.class);
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
        return modelMapper.map(permissionDTO, Permission.class);
    }

    private List<PermissionDTO> convertPermissionEntitiesToDTOs(Iterable<Permission> permissions) {
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionDTO permissionDTO = modelMapper.map(permission, PermissionDTO.class);
            permissionDTOList.add(permissionDTO);
        }
        return permissionDTOList;
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


    @Autowired
    AdminUserRepo adminRepo;
    @Autowired
    RetailUserRepo retailRepo;
    @Autowired
    OperationsUserRepo opRepo;
    @Autowired
    CorporateUserRepo corpRepo;


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
            ;
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
}
