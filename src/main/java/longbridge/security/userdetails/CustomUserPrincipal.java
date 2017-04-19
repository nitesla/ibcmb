package longbridge.security.userdetails;

import longbridge.models.Role;
import longbridge.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class CustomUserPrincipal implements UserDetails {


    private static final long serialVersionUID = 1L;

    private final User user;



    //

    public CustomUserPrincipal(User user) {
        this.user = user;

    }

    //

    @Override
    public String getUsername() {
        return user.getUserName();
    }


    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
//        for (final Permission privilege : user.getPrivileges()) {
//            authorities.add(new SimpleGrantedAuthority(privilege.getName()));
//        }


        System.out.println(user.getRole().getName());
       getPrivileges(user.getRole())

               .forEach(i-> authorities.add(new SimpleGrantedAuthority(i)));

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //TODO we will create a new constructor to factor out new things

    public User getUser() {
        return user;
    }





    private List<String> getPrivileges(Role roles) {

        List<String> privileges = new ArrayList<>();
        roles.getPermissions()
                .forEach(i -> privileges.add(i.getName()));




        return privileges;
    }



}
