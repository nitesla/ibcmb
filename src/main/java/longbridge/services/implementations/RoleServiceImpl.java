package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.models.*;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.RoleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RoleRepo roleRepo;

    private VerificationRepo verificationRepo;
    @Autowired
    private ModelMapper modelMapper;

    private PermissionRepo permissionRepo;

    @Autowired
    public RoleServiceImpl(RoleRepo roleRepo, VerificationRepo verificationRepo, PermissionRepo permissionRepo){
        this.roleRepo = roleRepo;
        this.verificationRepo = verificationRepo;
        this.permissionRepo = permissionRepo;
    }


    /**
     * Adds a new role to the system
     * @param  role the role to be added to the system
     */
    @Override
    public void add(RoleDTO role, AdminUser initiator) {
        try {
            Verification verification = new Verification();
            verification.setBeforeObject("");
            verification.setAfterObject(serialize(role));
            verification.setOriginal("");
            verification.setDescription("Added a new Role");
            verification.setOperationCode(OperationCode.ADD_CODE);
            verification.setInitiatedBy(initiator);
            verification.setInitiatedOn(new Date());
            verificationRepo.save(verification);

            logger.info("Role creation request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());
        }
        catch (Exception e){
            logger.error("Error Occurred {}",e);
        }
    }

    @Override
    public void modify(RoleDTO role, AdminUser initiator) {
        Role roleO = roleRepo.findOne(role.getId());
        RoleDTO originalObject = convertEntityToDTO(roleO);

        try {
            Verification verification = new Verification();
            verification.setBeforeObject(serialize(originalObject));
            verification.setAfterObject(serialize(role));
            verification.setOriginal(serialize(originalObject));
            verification.setDescription("Modified a Role");
            verification.setOperationCode(OperationCode.MODIFY_CODE);
            verification.setInitiatedBy(initiator);
            verification.setInitiatedOn(new Date());
            verificationRepo.save(verification);

            logger.info("Role modification request has been added. Before {}, After {} ", verification.getBeforeObject(), verification.getAfterObject());
        }
        catch (Exception e){
            logger.error("Error Occurred {}",e);
        }
    }

    @Override
    public RoleDTO deserialize(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RoleDTO role = mapper.readValue(data, RoleDTO.class);
        return role;
    }

    @Override
    public String serialize(RoleDTO role) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(role);
        return data;
    }

//    @Override
//    public Role deserialize(String data) throws IOException {
//        Role role = new Role();
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = mapper.readTree(data);
//        ObjectNode objectNode = nodeFactory.objectNode();
//        role.setId(objectNode.get("id").asLong());
//        role.setVersion(objectNode.get("version").asInt());
//        role.setName(objectNode.get("name").asText());
//        role.setEmail(objectNode.get("email").asText());
//        role.setUserType(UserType.valueOf(objectNode.get("userType").asText()));
//
//        //deserialize the listentity
//        List<String> ids = objectNode.findValuesAsText("permissions");
//        Collection<Permission> permissionCollection = new LinkedList<Permission>();
//        for(String id: ids){
//            Permission permission = new Permission();
//            permission.setId();
//        }
////        ArrayNode arrayNode = objectNode.putArray("permissions");
//        objectNode.put("permissions",serializeEntityList(role.getPermissions(), arrayNode));
//        return objectNode.toString();
//    }
//
////    @Override
//    public String serialize(Role role) throws JsonProcessingException {
//        ObjectNode objectNode = nodeFactory.objectNode();
//        objectNode.put("id", role.getId());
//        objectNode.put("email", role.getEmail());
//        objectNode.put("name", role.getName());
//        objectNode.put("userType", role.getUserType().toString());
////        ArrayNode arrayNode = objectNode.putArray("permissions");
//        objectNode.put("permissions",serializeEntityList(role.getPermissions(), arrayNode));
//        return objectNode.toString();
//    }

    @Override
    public void verify(Verification t, AdminUser verifier) throws IOException {
        if(t.getVerifiedBy() != null){
            //already verified
            logger.debug("Already verified");
            return ;
        }
        t.setVerifiedBy(verifier);
        t.setVerifiedOn(new Date());

        RoleDTO afterRoleDTO = deserialize(t.getAfterObject());
        Role afterRole = convertDTOToEntity(afterRoleDTO);

        //handle relationships
        Collection<Permission> permissions = afterRole.getPermissions();
        afterRole.setPermissions(new LinkedList<>());
        permissions.stream().forEach(permission -> {
            afterRole.getPermissions().add(convertDTOToEntity(getPermission(permission.getId())));
        });

        roleRepo.save(afterRole);
        t.setEntityId(afterRole.getId());
        verificationRepo.save(t);
    }

    @Override
    public void decline(Verification verification, AdminUser decliner, String declineReason) {
        verification.setDeclinedBy(decliner);
        verification.setDeclinedOn(new Date());
        verification.setDeclineReason(declineReason);
        verificationRepo.save(verification);
    }


    @Override
    public void updateRole(Role role) {
        roleRepo.save(role);
    }

    @Override
    public void deleteRole(Role role) {
        role.setDelFlag("Y");
        roleRepo.save(role);
    }

    @Override
    public void addPermission(Permission permission) {
        permissionRepo.save(permission);
    }

    @Override
    public void updatePermission(Permission permission) {
        permissionRepo.save(permission);
    }

    @Override
    public void deletePermission(Permission permission) {
        permission.setDelFlag("Y");
        permissionRepo.save(permission);
    }

    @Override
    public void addRole(RoleDTO roleDTO) {
        Role role = convertDTOToEntity(roleDTO);
        roleRepo.save(role);
    }

    @Override
    public RoleDTO getRole(Long id) {
        Role role = roleRepo.findOne(id);
        return convertEntityToDTO(role);
    }

    @Override
    public Iterable<RoleDTO> getRoles() {
        Iterable<Role> roles = roleRepo.findAll();
        return convertRoleEntitiesToDTOs(roles);
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        Role role =convertDTOToEntity(roleDTO);
        roleRepo.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepo.findOne(id);
        role.setDelFlag("Y");
        roleRepo.save(role);
    }

    public void addPermission(PermissionDTO permissionDTO) {
        Permission permission = convertDTOToEntity(permissionDTO);
        permissionRepo.save(permission);
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
    public void updatePermission(PermissionDTO permissionDTO) {
        Permission permission =convertDTOToEntity(permissionDTO);
        permissionRepo.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        Permission permission = permissionRepo.findOne(id);
        permission.setDelFlag("Y");
        permissionRepo.save(permission);
    }



    private RoleDTO convertEntityToDTO(Role role){
        return modelMapper.map(role,RoleDTO.class);
    }

    private Role convertDTOToEntity(RoleDTO roleDTO){
        return modelMapper.map(roleDTO,Role.class);
    }

    private List<RoleDTO> convertRoleEntitiesToDTOs(Iterable<Role> roles){
        List<RoleDTO> roleDTOList = new ArrayList<>();
        for(Role role: roles){
            RoleDTO roleDTO = modelMapper.map(role,RoleDTO.class);
            roleDTOList.add(roleDTO);
        }
        return roleDTOList;
    }

    private PermissionDTO convertEntityToDTO(Permission permission){
        return modelMapper.map(permission,PermissionDTO.class);
    }

    private Permission convertDTOToEntity(PermissionDTO permissionDTO){
        return modelMapper.map(permissionDTO,Permission.class);
    }

    private List<PermissionDTO> convertPermissionEntitiesToDTOs(Iterable<Permission> permissions){
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        for(Permission permission: permissions){
            PermissionDTO permissionDTO = modelMapper.map(permission,PermissionDTO.class);
            permissionDTOList.add(permissionDTO);
        }
        return permissionDTOList;
    }






}
