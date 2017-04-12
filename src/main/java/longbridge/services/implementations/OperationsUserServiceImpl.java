package longbridge.services.implementations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.models.OperationsUser;
import longbridge.repositories.OperationsUserRepo;
import longbridge.services.OperationsUserService;
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
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
@Service
public class OperationsUserServiceImpl implements OperationsUserService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private OperationsUserRepo opUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public OperationsUserServiceImpl() {

    }

    @Autowired
    public OperationsUserServiceImpl(OperationsUserRepo opUserRepo, BCryptPasswordEncoder passwordEncoder) {
        this.opUserRepo = opUserRepo;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public OperationsUser getUser(Long id)
    {
        return this.opUserRepo.findOne(id);
    }

    @Override
    public Iterable<OperationsUser> getUsers()
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

//         User.setPassword(this.passwordEncoder.encode(User.getPassword()));

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
    public boolean updateUser(OperationsUser user) {
        return false;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public void resetPassword(OperationsUser user) {

    }

    @Override
    public boolean changePassword(OperationsUser User, String oldPassword, String newPassword) {
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

    private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser){
        return  modelMapper.map(operationsUser,OperationsUserDTO.class);
    }

    private OperationsUser convertDTOToEntity(OperationsUserDTO operationsUserDTO){
        return  modelMapper.map(operationsUserDTO,OperationsUser.class);
    }

    private List<OperationsUserDTO> convertEntitiesToDTOs(Iterable<OperationsUser> operationsUsers){
        List<OperationsUserDTO> operationsUserDTOList = new ArrayList<>();
        for(OperationsUser operationsUser: operationsUsers){
            OperationsUserDTO userDTO =  convertEntityToDTO(operationsUser);
            operationsUserDTOList.add(userDTO);
        }
        return operationsUserDTOList;
    }

	@Override
	public Page<OperationsUserDTO> getUsers(Pageable pageDetails) {
        Page<OperationsUser> page = opUserRepo.findAll(pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}

}
