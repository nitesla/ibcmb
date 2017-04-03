package longbridge.services.implementations;

import longbridge.models.OperationsUser;
import longbridge.repositories.UserRepo;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collection;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
@Service("OperationsUserService")
public class OperationsUserServiceImpl implements OperationsUserService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private UserRepo<OperationsUser, Long> opUserRepo;
    private BCryptPasswordEncoder passwordEncoder;


    public OperationsUserServiceImpl() {

    }

    @Autowired

    public OperationsUserServiceImpl(UserRepo<OperationsUser, Long> userRepo, BCryptPasswordEncoder passwordEncoder) {
          this.opUserRepo = opUserRepo;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public OperationsUser getUser(Long id)
    {
        return this.opUserRepo.findOne(id);
    }

    @Override
    public Collection<OperationsUser> getUsers()
    {
        return this.opUserRepo.findAll();
    }

    @Override
    public void setPassword(OperationsUser User, String password) {

    }

    @Override
    public boolean addUser(OperationsUser User) {
     boolean ok= false;

     try {

         User.setPassword(this.passwordEncoder.encode(User.getPassword()));

         this.opUserRepo.save(User);
         logger.info("USER {} HAS BEEN ADDED ",User.getId());
         ok=true;
     }
     catch (Exception e){
         logger.error("ERROR OCCURRED {}",e.getMessage());

     }
     return ok;
    }

    @Override
    public void resetPassword(OperationsUser user) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean changePassword(OperationsUser User,String oldPassword, String newPassword) {
      boolean ok=false;


      try {
          if (getUser(User.getId()) == null) {
              logger.error("USER DOES NOT EXIST");
              return ok;
          }

          if (this.passwordEncoder.matches(oldPassword,User.getPassword())){
             User.setPassword( this.passwordEncoder.encode(newPassword));
              this.opUserRepo.save(User);
              logger.info("USER {}'s password has been updated",User.getId());
              ok=true;
          }else{
             logger.error("INVALID CURRENT PASSWORD FOR USER {}",User.getId());
          }


      }catch (Exception e){
              e.printStackTrace();
          logger.error("ERROR OCCURRED {}",e.getMessage());
      }
      return ok;
    }

    @Override
    public void generateAndSendPassword()
    {


  //TODO

    }



}
