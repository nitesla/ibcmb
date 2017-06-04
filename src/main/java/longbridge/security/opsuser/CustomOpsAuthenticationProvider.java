package longbridge.security.opsuser;

import longbridge.models.OperationsUser;
import longbridge.models.RetailUser;
import longbridge.repositories.OperationsUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Created by ayoade_farooq@yahoo.com on 5/19/2017.
 */
//@Component
//public class CustomOpsAuthenticationProvider  implements AuthenticationProvider {
//    @Autowired
//    private OperationsUserRepo userRepo;
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String userName = authentication.getName();
//
//        OperationsUser user = userRepo.findFirstByUserName(userName);
//
//       if(user.getEntrustId()!=null){
//
//
//     //return new UsernamePasswordAuthenticationToken();
//
//
//       }
//
//
//
//
//
//        return null;
//    }
//
//    @Override
//    public boolean supports(Class<?> aClass) {
//        return false;
//    }
//}
