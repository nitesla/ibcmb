package longbridge.utils;

import longbridge.security.userdetails.CustomUserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 6/20/2017.
 */
@Service
public class HostMaster {


    public  String getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }

        return "";
    }

    public  boolean isPasswordExpired(){
       try{
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           CustomUserPrincipal userPrincipal= (CustomUserPrincipal)authentication.getPrincipal();
           return !userPrincipal.isCredentialsNonExpired();
       }catch (Exception e){
          e.printStackTrace();
          return true;
       }

    }

}
