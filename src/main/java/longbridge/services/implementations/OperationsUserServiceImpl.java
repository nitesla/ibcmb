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

    private OperationsUserRepo operationsUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public OperationsUserServiceImpl() {

    }

    @Autowired
    public OperationsUserServiceImpl(OperationsUserRepo operationsUserRepo, BCryptPasswordEncoder passwordEncoder) {
        this.operationsUserRepo = operationsUserRepo;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public OperationsUserDTO getUser(Long id) {
        OperationsUser user = operationsUserRepo.findOne(id);
        return convertEntityToDTO(user);
    }

    @Override
    public Iterable<OperationsUserDTO> getUsers() {
        Iterable<OperationsUser> operationsUsers = operationsUserRepo.findAll();
        return convertEntitiesToDTOs(operationsUsers);
    }

    @Override
    public void setPassword(OperationsUser user, String password) {

    }

    @Override
    public boolean addUser(OperationsUserDTO userDTO) {
     boolean ok= false;
     try {
         OperationsUser user = convertDTOToEntity(userDTO);
         user.setPassword(this.passwordEncoder.encode(user.getPassword()));
         this.operationsUserRepo.save(user);
         logger.info("Created Operation User: {}",user.getUserName());
         ok=true;
     }
     catch (Exception e){
         logger.error("Could not create the  {}",e.getMessage());

     }
     return ok;
    }

    @Override
    public boolean updateUser(OperationsUserDTO userDTO) {
        boolean ok= false;
        OperationsUser user = convertDTOToEntity(userDTO);
        operationsUserRepo.save(user);
        ok=true;

        return ok;
    }

    @Override
    public void deleteUser(Long userId) {
        operationsUserRepo.delete(userId);
    }

    @Override
    public void resetPassword(OperationsUser user) {

    }

    @Override
    public boolean changePassword(OperationsUserDTO userDTO, String oldPassword, String newPassword) {
      boolean ok=false;

      try {
          if (getUser(userDTO.getId()) == null) {
              logger.error("USER DOES NOT EXIST");
              return ok;
          }

          if (this.passwordEncoder.matches(oldPassword,userDTO.getPassword())){
              OperationsUser user = convertDTOToEntity(userDTO);
             user.setPassword( this.passwordEncoder.encode(newPassword));
              this.operationsUserRepo.save(user);
              logger.info("USER {}'s password has been updated",user.getId());
              ok=true;
          }else{
             logger.error("INVALID CURRENT PASSWORD FOR USER {}",userDTO.getId());
          }

      }catch (Exception e){
              e.printStackTrace();
          logger.error("ERROR OCCURRED {}",e.getMessage());
      }
      return ok;
    }

    @Override
    public void generateAndSendPassword() {
  //TODO

    }

    private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser){
        return  modelMapper.map(operationsUser,OperationsUserDTO.class);
    }

    private OperationsUser convertDTOToEntity(OperationsUserDTO operationsUserDTO){
        return  modelMapper.map(operationsUserDTO,OperationsUser.class);
    }

    private Iterable<OperationsUserDTO> convertEntitiesToDTOs(Iterable<OperationsUser> operationsUsers){
        List<OperationsUserDTO> operationsUserDTOList = new ArrayList<>();
        for(OperationsUser operationsUser: operationsUsers){
            OperationsUserDTO userDTO =  convertEntityToDTO(operationsUser);
            operationsUserDTOList.add(userDTO);
        }
        return operationsUserDTOList;
    }

	@Override
	public Page<OperationsUserDTO> getUsers(Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

}
