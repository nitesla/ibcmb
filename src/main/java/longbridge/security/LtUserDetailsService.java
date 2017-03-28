package longbridge.security;

import longbridge.models.Customer;
import longbridge.models.Permission;
import longbridge.models.Profile;
import longbridge.models.Role;
import longbridge.services.CustomerService;
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
    private CustomerService customerService;
    //private RoleRepo roleRepository;

//    public LtUserDetailsService(CustomerService custService){
//        customerService = custService;
//    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        try {
            Customer customer = customerService.findByEmail(email);

            if (customer == null) {
                throw new UsernameNotFoundException("No user found with username: " + email);
            }

            return new org.springframework.security.core.userdetails.User(
                    customer.getEmail(), customer.getPassword(), customer.isEnabled(), true, true,
                    true, getAuthorities(customer.getProfiles(), customer.getRoles()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Profile> profiles, Collection<Role> roles) {

        return getGrantedAuthorities(getPrivileges(profiles, roles));
    }

    private List<String> getPrivileges(Collection<Profile> profiles, Collection<Role> roles) {

        List<String> permissions = new ArrayList<>();
        List<Permission> collection = new ArrayList<>();
        for (Role role : roles) {
            permissions.add(role.getName());
        }
        for (Profile profile : profiles) {
            permissions.add(profile.getName());
        }
        for (Profile profile : profiles) {
            collection.addAll(profile.getPermissions());
        }
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

//    public Collection<? extends GrantedAuthority> getAuthorities(Profile profile, Role role) {
//
//        Set<SimpleGrantedAuthority> authList = new TreeSet<SimpleGrantedAuthority>();
//
//        //for (Role role : roles) {
//        authList.addAll(getGrantedAuthorities(profile, role));
//        //}
//
//        return authList;
//    }
//
//    /**
//     * Wraps a {@link Profile} role to {@link SimpleGrantedAuthority} objects
//     *
//     * @param profile
//     *            {@link String} of roles
//     * @return list of granted authorities
//     */
//    public static Set<SimpleGrantedAuthority> getGrantedAuthorities(Profile profile, Role role) {
//
//        Set<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();
//        authorities.add(new SimpleGrantedAuthority(role.getName()));
//        authorities.add(new SimpleGrantedAuthority(profile.getName()));
//        Collection<Permission> profilePermissions = profile.getPermissions();
//        for (Permission permission : profilePermissions) {
//            authorities.add(new SimpleGrantedAuthority(permission.getName()));
//        }
//
//        //System.out.println(authorities);
//        return authorities;
//    }
//
////    private Collection<? extends GrantedAuthority> getAuthorities(
////            Collection<Profile> profiles, Collection<Role> roles) {
////
////        return getGrantedAuthorities(getPermissions(profiles, roles));
////    }
////
////    private List<GrantedAuthority> getGrantedAuthorities(List<String> Permissions) {
////        List<GrantedAuthority> authorities = new ArrayList<>();
////        for (String Permission : Permissions) {
////            authorities.add(new SimpleGrantedAuthority(Permission));
////        }
////        return authorities;
////    }
////
////    private List<String> getPermissions(Collection<Profile> profiles, Collection<Role> roles) {
////
////        List<String> Permissions = new ArrayList<>();
////        List<Permission> collection = new ArrayList<>();
////        for (Profile profile : profiles) {
////            collection.addAll(profile.getPermissions());
////        }
////        for (Permission item : collection) {
////            Permissions.add(item.getName());
////        }
////        return Permissions;
////    }


}