package longbridge.services;

import longbridge.models.User;
import longbridge.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Service
public class UserService {
    @Autowired
    private UserRepo userRepository;

    //@PreAuthorize("hasAuthority('CREATE_USER')")
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
