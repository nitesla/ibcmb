package longbridge.security.opsuser;

import longbridge.models.OperationsUser;
import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.repositories.OperationsUserRepo;

import longbridge.security.userdetails.CustomUserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("operationUserDetails")
public class OperationUserDetailsService implements UserDetailsService {

    private OperationsUserRepo operationsUserRepo;

    public OperationUserDetailsService(OperationsUserRepo operationsUserRepo) {
        this.operationsUserRepo = operationsUserRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        OperationsUser user= operationsUserRepo.findByUserName(s);
        return new CustomUserPrincipal(user);
    }



}
