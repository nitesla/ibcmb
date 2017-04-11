package longbridge.security.retailuser;


import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("retailUserDetails")
public class RetailUserDetailsService implements UserDetailsService {

    private RetailUserRepo retailUserRepo;

    public RetailUserDetailsService(RetailUserRepo retailUserRepo) {
        this.retailUserRepo = retailUserRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        RetailUser user= retailUserRepo.findByUserName(s);
        return new CustomUserPrincipal(user);
    }



}
