package longbridge.security.adminuser;

import longbridge.models.AdminUser;
import longbridge.repositories.AdminUserRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("adminUserDetails")
public class AdminUserDetailsService implements UserDetailsService {

    private AdminUserRepo adminUserRepo;

    public AdminUserDetailsService(AdminUserRepo adminUserRepo) {
        this.adminUserRepo = adminUserRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        AdminUser user= adminUserRepo.findByUserName(s);
        return new CustomUserPrincipal(user);
    }



}
