package longbridge.services.implementations;

import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.UserRepo;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by SYLVESTER on 3/29/2017.
 */

@Service
public class RetailUserServiceImpl implements RetailUserService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private RetailUserRepo retailUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

    public RetailUserServiceImpl(){

    }

    @Autowired
    public  RetailUserServiceImpl(RetailUserRepo retailUserRepo, BCryptPasswordEncoder passwordEncoder) {
        this.retailUserRepo = retailUserRepo;
        this.passwordEncoder=passwordEncoder;
    }


    @Override
    public RetailUser getUser(Long id) {
        return this.retailUserRepo.findOne(id) ;
    }

    @Override
    public Collection<RetailUser> getUsers() {
        return this.retailUserRepo.findAll();
    }

    @Override
    public boolean setPassword(RetailUser user, String password) {
        boolean ok=false;
        if(user!=null) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }
        else{throw new RuntimeException("NO USER FOUND");}
        return ok;

    }

    @Override

    public boolean addUser(RetailUser user)
    {
        boolean ok=false;
        /*Get the user's details from the model */
        if(user!=null){
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            this.retailUserRepo.save(user);
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
    public boolean updateUser(RetailUser user) {
        boolean ok=false;
        try {
            if(user!=null) {
                this.retailUserRepo.save(user);
                logger.info("USER SUCCESSFULLY UPDATED");
            }
        }
        catch(Exception e){e.printStackTrace();}
        return ok;
    }

    @Override
    public boolean generateAndSendPassword(RetailUser user) {
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
        /*String password=   RandomStringUtils.randomNumeric(10);
        return password!=null? password: "";*/
        return null;
    }



    public boolean sendPassword(RetailUser user){
        //TODO use an smtp server to send new password to user via mail
        return false;
    }
}
