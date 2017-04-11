package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import longbridge.models.*;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RoleRepo roleRepo;

    private VerificationRepo verificationRepo;

    private PermissionRepo permissionRepo;
    private JsonNodeFactory nodeFactory = new JsonNodeFactory(false);

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
    public void add(Role role, AdminUser initiator) {
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
    public void modify(Role role, AdminUser initiator) {
        Role originalObject = roleRepo.findOne(role.getId());

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
    public Role deserialize(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Role role = mapper.readValue(data, Role.class);
        return role;
    }

    @Override
    public String serialize(Role role) throws JsonProcessingException {
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

        Role afterRole = deserialize(t.getAfterObject());

        //handle relationships
        Collection<Permission> permissions = afterRole.getPermissions();
        afterRole.setPermissions(new LinkedList<>());
        permissions.stream().forEach(permission -> {
            afterRole.getPermissions().add(getPermission(permission.getId()));
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
    public Role getRole(Long id) {
        return roleRepo.findOne(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepo.findAll();
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
    public List<Permission> getPermissions() {
        return permissionRepo.findAll();
    }

    @Override
    public void addPermission(Permission permission) {
        permissionRepo.save(permission);
    }

    @Override
    public Permission getPermission(Long id) {
        return permissionRepo.findOne(id);
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

}
