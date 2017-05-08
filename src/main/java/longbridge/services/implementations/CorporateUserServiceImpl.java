package longbridge.services.implementations;

import longbridge.dtos.CorporateUserDTO;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.CorporateUserService;
import longbridge.services.SecurityService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/4/2017.
 */
@Service
public class CorporateUserServiceImpl implements CorporateUserService {

    private CorporateUserRepo corporateUserRepo;

    private CorpLimitRepo corpLimitRepo;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityService securityService;

    @Autowired
    ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorporateUserServiceImpl(CorporateUserRepo corporateUserRepo, BCryptPasswordEncoder passwordEncoder, SecurityService securityService) {
        this.corporateUserRepo = corporateUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public CorporateUserDTO getUser(Long id) {
        CorporateUser corporateUser = corporateUserRepo.findOne(id);
        return convertEntityToDTO(corporateUser);
    }

    @Override
    public CorporateUser getUserByName(String username) {
        return corporateUserRepo.findByUserName(username);
    }

    @Override
    public Iterable<CorporateUserDTO> getUsers(Corporate corporate) {

        Iterable<CorporateUser> corporateUserDTOList = corporateUserRepo.findAll();
        return convertEntitiesToDTOs(corporateUserDTOList);
    }

    @Override
    public Iterable<CorporateUser> getUsers() {
        return corporateUserRepo.findAll();
    }

    @Override
    public void setPassword(CorporateUser user, String hashedPassword) {

    }

    @Override
    public boolean updateUser(CorporateUser user) {
        return false;
    }

    @Override
    public void addUser(CorporateUser user) {
        corporateUserRepo.save(user);
    }

    @Override
    public void resetPassword(CorporateUser user) {

    }

    @Override
    public void deleteUser(Long userId) {
        CorporateUser repo = corporateUserRepo.findOne(userId);
        repo.setDelFlag("Y");
        corporateUserRepo.save(repo);
    }

    @Override
    public void enableUser(CorporateUser user) {
        user.setEnabled(true);
        corporateUserRepo.save(user);
    }

    @Override
    public void disableUser(CorporateUser user) {
        user.setEnabled(false);
        corporateUserRepo.save(user);
    }

    @Override
    public void lockUser(CorporateUser user, Date unlockat) {
        //todo
    }

    @Override
    public void changePassword(CorporateUser user, String oldPassword, String newPassword) {
        try {
            if (user == null) {
                logger.error("User does not exist");
                return;
            }

            if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(newPassword));
                corporateUserRepo.save(user);
                logger.info("User {}'s password has been updated", user.getUserName());
                return;
            } else {
                logger.error("Invalid current password supplied for {}", user.getUserName());
                return;
            }
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
        }
    }

    @Override
    public void generateAndSendPassword(CorporateUser user) {
        String newPassword = securityService.generatePassword();
        try {
            if (user == null) {
                logger.error("User does not exist");
                return;
            }
            user.setPassword(this.passwordEncoder.encode(newPassword));
            corporateUserRepo.save(user);
            logger.info("USER {}'s password has been updated", user.getUserName());
            //todo send the email.. messagingService.sendEmail(EmailDetail);
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
        }
    }


    private CorporateUserDTO convertEntityToDTO(CorporateUser CorporateUser){
        return  modelMapper.map(CorporateUser,CorporateUserDTO.class);
    }

    private CorporateUser convertDTOToEntity(CorporateUserDTO CorporateUserDTO){
        return  modelMapper.map(CorporateUserDTO,CorporateUser.class);
    }


    private List<CorporateUserDTO> convertEntitiesToDTOs(Iterable<CorporateUser> corporateUsers){
        List<CorporateUserDTO> corporateUserDTOList = new ArrayList<>();
        for(CorporateUser corporateUser: corporateUsers){
            CorporateUserDTO corporateUserDTO =  convertEntityToDTO(corporateUser);
            corporateUserDTOList.add(corporateUserDTO);
        }
        return corporateUserDTOList;
    }

	@Override
	public Page<CorporateUserDTO> getUsers(Corporate Corporate, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<CorporateUserDTO> getUsers(Pageable pageDetails) {
		// TODO Auto-generated method stub

        Page<CorporateUser> page = corporateUserRepo.findAll(pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}


}
