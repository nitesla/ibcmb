package longbridge.services.implementations;

import longbridge.dtos.RetailUserDTO;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.AccountService;
import longbridge.services.RetailUserService;
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
import java.util.List;

/**
 * Created by SYLVESTER on 3/29/2017.
 */

@Service
public class RetailUserServiceImpl implements RetailUserService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private RetailUserRepo retailUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AccountService accountService;

    @Autowired
    ModelMapper modelMapper;

    public RetailUserServiceImpl(){

    }

    @Autowired
    public  RetailUserServiceImpl(RetailUserRepo retailUserRepo, BCryptPasswordEncoder passwordEncoder) {
        this.retailUserRepo = retailUserRepo;
        this.passwordEncoder=passwordEncoder;
    }


    @Override
    public RetailUserDTO getUser(Long id) {
        RetailUser retailUser =this.retailUserRepo.findOne(id) ;
        return convertEntityToDTO(retailUser);
    }

    @Override
    public Iterable<RetailUserDTO> getUsers() {
        Iterable<RetailUser> retailUsers =retailUserRepo.findAll();
        logger.info("RetailUsers {}",retailUsers.toString());
         return convertEntitiesToDTOs(retailUsers);
    }

    @Override
    public boolean setPassword(RetailUser user, String password) {
        boolean ok=false;
        if(user!=null) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }
        else{throw new RuntimeException("Null user provided");}
        return ok;

    }

    @Override
    public boolean addUser(RetailUserDTO user)
    {
        boolean ok=false;
        /*Get the user's details from the model */
        if(user!=null){
//            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            RetailUser retailUser = convertDTOToEntity(user);
            this.retailUserRepo.save(retailUser);
            logger.info("USER {} HAS BEEN CREATED");
        }
        else{logger.error("USER NOT FOUND");}

        return ok;
    }

    @Override
    public void deleteUser(Long userId) {
        retailUserRepo.delete(userId);
    }

    @Override
    public boolean resetPassword(RetailUser user, String newPassword) {
        boolean ok = false;

        if(user!=null)
        {
            setPassword(user,newPassword);

            logger.info("PASSWORD RESET SUCCESSFULLY");
        }

        return ok;
    }

    @Override
    public boolean changePassword(RetailUser user,String oldPassword, String newPassword) {
        boolean ok=false;

        try {

            if (getUser(user.getId()) == null) {

                if (getUser(user.getId()) == null) {

                    logger.error("USER DOES NOT EXIST");
                    return ok;
                }

                if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                    user.setPassword(this.passwordEncoder.encode(newPassword));
                    this.retailUserRepo.save(user);
                    logger.info("USER {}'s password has been updated", user.getId());
                    ok = true;
                } else {
                    logger.error("INVALID CURRENT PASSWORD FOR USER {}", user.getId());

                    if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                        user.setPassword(this.passwordEncoder.encode(newPassword));
                        this.retailUserRepo.save(user);
                        logger.info("USER {}'s password has been updated", user.getId());
                        ok = true;
                    } else {
                        logger.error("INVALID CURRENT PASSWORD FOR USER {}", user.getId());

                    }

                }
            }
        }
            catch (Exception e){
                    e.printStackTrace();
                    logger.error("ERROR OCCURRED {}",e.getMessage());
                }
                return ok;
    }

    @Override
    public boolean updateUser(RetailUserDTO user) {
        boolean ok=false;
        try {
            if(user!=null) {
                RetailUser retailUser = convertDTOToEntity(user);
                this.retailUserRepo.save(retailUser);
                logger.info("USER SUCCESSFULLY UPDATED");
            }
        }
        catch(Exception e){e.printStackTrace();}
        return ok;
    }

    @Override
    public boolean AddAccount(RetailUser user, Account account) {
       return accountService.AddAccount(user.getCustomerId(),account);
    }

    @Override
    public boolean generateAndSendPassword(RetailUser user) {
        boolean ok = false;

        try {
            String newPassword = generatePassword();
            setPassword(user, newPassword);
            retailUserRepo.save(user);

            sendPassword(user);
            logger.info("PASSWORD GENERATED AND SENT");
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }



        return ok;

    }
    public String generatePassword(){
        /*String password=   RandomStringUtils.randomNumeric(10);
        return password!=null? password: "";*/
        return null;
    }



    public boolean sendPassword(RetailUser user){
        //TODO use an smtp server to send new password to user via mail
        return false;
    }

    private RetailUserDTO convertEntityToDTO(RetailUser RetailUser){
        return  modelMapper.map(RetailUser,RetailUserDTO.class);
    }

    private RetailUser convertDTOToEntity(RetailUserDTO RetailUserDTO){
        return  modelMapper.map(RetailUserDTO,RetailUser.class);
    }

    private List<RetailUserDTO> convertEntitiesToDTOs(Iterable<RetailUser> RetailUsers){
        List<RetailUserDTO> retailUserDTOList = new ArrayList<>();
        for(RetailUser retailUser: RetailUsers){
          RetailUserDTO userDTO =  convertEntityToDTO(retailUser);
          retailUserDTOList.add(userDTO);
        }
        return retailUserDTOList;
    }

	@Override
	public Page<RetailUserDTO> getUsers(Pageable pageDetails) {
        Page<RetailUser> page = retailUserRepo.findAll(pageDetails);
        List<RetailUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<RetailUserDTO> pageImpl = new PageImpl<RetailUserDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}

}
