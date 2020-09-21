package longbridge.services.implementations;

import longbridge.models.LoggedUser;
import longbridge.models.User;
import longbridge.repositories.LoggedUserRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 8/30/2017.
 */
@Service
public class LoggedUserServiceImpl implements LoggedUserService {

    private LoggedUserRepo loggedUserRepo;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setLoggedUserRepo(LoggedUserRepo loggedUserRepo) {
        this.loggedUserRepo = loggedUserRepo;
    }

    @Override
    public boolean sessionExists(String s) {
        return loggedUserRepo.existsFirstBySessionId(s);
    }

    @Override
    public void loginUser(String sessionId) {
        if (getCurrentUser() != null) {
            LoggedUser loggedUser = new LoggedUser();
            loggedUser.setSessionId(sessionId);
            loggedUser.setUsername(getCurrentUser().getUserName());
            loggedUser.setUserType(getCurrentUser().getUserType().toString());
            loggedUserRepo.save(loggedUser);
        }

    }

    @Override
    public void logOutUser(String sessionId) {
     String sql ="delete from logged_user where session_id =?";

        jdbcTemplate.update(
                sql,

                sessionId);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }

        return null;
    }
}
