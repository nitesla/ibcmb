package longbridge.security.userdetails;


import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.RetailUserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("corporateUserDetails")
public class CorporateUserDetailsService implements UserDetailsService {

    private CorporateUserRepo corporateUserRepo;

    public CorporateUserDetailsService(CorporateUserRepo corporateUserRepo) {
        this.corporateUserRepo = corporateUserRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        CorporateUser user= corporateUserRepo.findByUserName(s);
        return new CustomUserPrincipal(user);
    }



}
