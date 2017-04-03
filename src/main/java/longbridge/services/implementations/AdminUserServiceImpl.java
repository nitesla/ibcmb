package longbridge.services.implementations;

import longbridge.models.AdminUser;
import longbridge.repositories.UserRepo;
import longbridge.services.AdminUserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * Created by SYLVESTER on 3/30/2017.
 */
public class AdminUserServiceImpl implements AdminUserService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    private UserRepo<AdminUser, Long> adminUserLongUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

@Autowired
public AdminUserServiceImpl(UserRepo<AdminUser, Long> adminUserLongUserRepo, BCryptPasswordEncoder passwordEncoder){

    this.adminUserLongUserRepo=adminUserLongUserRepo;
    this.passwordEncoder=passwordEncoder;
}
    public AdminUserServiceImpl(){

    }

    @Override
    public AdminUser getUser(Long id) {
        return this.adminUserLongUserRepo.findOne(id);
    }

    @Override
    public Iterable<AdminUser> getUsers() {
        return this.adminUserLongUserRepo.findAll();
    }

    @Override
    public boolean setPassword(AdminUser user, String hashedPassword) {
        boolean ok=false;
        if(user!=null) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            updateUser(user);
        }
        else{throw new RuntimeException("NO USER FOUND");}
        return ok;
    }

    @Override
    public boolean addUser(AdminUser user) {
        boolean ok=false;
        /*Get the user's details from the model */

        if(user!=null){
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            this.adminUserLongUserRepo.save(user);
            logger.info("USER {} HAS BEEN CREATED");
        }
        else{logger.error("USER NOT FOUND");}

        return ok;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        boolean ok = false;
        AdminUser user=   getUser(userId);
        if(user!=null)
        {
            setPassword(user,newPassword);

            logger.info("PASSWORD RESET SUCCESSFULLY");
        }

        return ok;
    }

    @Override
    public boolean changePassword(AdminUser user,String oldPassword, String newPassword) {
        boolean ok = false;


        try {

            if (getUser(user.getId()) == null) {

                if (getUser(user.getId()) == null) {

                    logger.error("USER DOES NOT EXIST");
                    return ok;
                }


                if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                    user.setPassword(this.passwordEncoder.encode(newPassword));
                    this.adminUserLongUserRepo.save(user);
                    logger.info("USER {}'s password has been updated", user.getId());
                    ok = true;
                } else {
                    logger.error("INVALID CURRENT PASSWORD FOR USER {}", user.getId());

                    if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                        user.setPassword(this.passwordEncoder.encode(newPassword));
                        this.adminUserLongUserRepo.save(user);
                        logger.info("USER {}'s password has been updated", user.getId());
                        ok = true;
                    } else {
                        logger.error("INVALID CURRENT PASSWORD FOR USER {}", user.getId());

                    }


                }
            }
        } catch (Exception e){
                e.printStackTrace();
                logger.error("ERROR OCCURRED {}",e.getMessage());
            }
            return ok;
        }

    @Override
    public boolean generateAndSendPassword(AdminUser user) {
        boolean ok = false;

        try {
            String newPassword = generatePassword();
            setPassword(user, newPassword);
            updateUser(user);

            sendPassword(user);
            logger.info("PASSWORD GENERATED AND SENT");
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }



        return ok;
    }

    public String generatePassword(){
       /* String password=   RandomStringUtils.randomNumeric(10);
        return password!=null? password: "";*/
        return  null;
    }



    public boolean sendPassword(AdminUser user){
        //TODO use an smtp server to send new password to user via mail
        return false;
    }
    public boolean updateUser(AdminUser user) {
        boolean ok=false;
        try {
            if(user!=null) {
                this.adminUserLongUserRepo.save(user);
                logger.info("USER SUCCESSFULLY UPDATED");
            }
        }
        catch(Exception e){e.printStackTrace();}
        return ok;
    }
}



