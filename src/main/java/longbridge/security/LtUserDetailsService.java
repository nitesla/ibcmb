package longbridge.security;

import longbridge.models.Permission;
import longbridge.models.Profile;
import longbridge.models.Role;
import longbridge.models.User;
import longbridge.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Showboy on 27/03/2017.
 */
@Service("userDetailsService")
@Transactional
public class LtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;
    //private RoleRepo roleRepository;

//    public LtUserDetailsService(CustomerService custService){
//        customerService = custService;
//    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        try {
            User user = userService.findByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException("No user found with username: " + email);
            }

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(), user.getPassword(), user.isEnabled(), true, true,
                    true, getAuthorities(user.getProfile(), user.getRole()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Profile profile, Role role) {

        return getGrantedAuthorities(getPrivileges(profile, role));
    }

    private List<String> getPrivileges(Profile profile, Role role) {

        List<String> permissions = new ArrayList<>();
        List<Permission> collection = new ArrayList<>();
        //for (Role role : roles) {
            permissions.add(role.getName());
        //}
        //for (Profile profile : profiles) {
            permissions.add(profile.getName());
        //}
        //for (Profile profile : profiles) {
            collection.addAll(profile.getPermissions());
        //}
        for (Permission item : collection) {
            permissions.add(item.getName());
        }
        return permissions;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

}