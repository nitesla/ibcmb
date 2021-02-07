package longbridge.security.api;

import longbridge.apiLayer.data.ResponseData;
import longbridge.security.corpuser.CorporateUserDetailsService;
import longbridge.security.retailuser.RetailUserDetailsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("apiUserDetails")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String AUTH_HEADER;
    @Autowired
    RetailUserDetailsService retailUserDetailsService;

    @Autowired
    CorporateUserDetailsService corporateUserDetailsService;



    private String getToken( HttpServletRequest request ) {

        String authHeader = request.getHeader(AUTH_HEADER);
        if ( authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }

        return null;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String error ;
        String authToken = getToken( request );
        Boolean isCorpUser = false;
        UserDetails userDetails;
        Boolean validate;


        if (authToken != null) {
            // Get username from token
            String username = jwtTokenUtil.getUsernameFromToken( authToken );

            if(username == null) {
                error = "Invalid token passed. You are probably using an expired token. Kindly generate another and try again";
                response.getWriter().write(processErrorMessage(error));
                return;
            }

            // Get user
            if(username.contains(":")){
                isCorpUser = true;
                try {
                    userDetails = corporateUserDetailsService.loadUserByUsername(username);
                    logger.info("corporate user details authentication " + userDetails);
                    isCorpUser = true;
                    validate = jwtTokenUtil.validateToken(authToken, userDetails, isCorpUser);
                    if (validate) {
                        grantAccess(userDetails, authToken);
                        logger.info("user %s has been granted access");
                    } else {
                        error = "Token could not be validated as it does not pass data integrity test. Kindly generate another and try again";
                        response.getWriter().write(processErrorMessage(error));
                        return;
                    }
                }catch(Exception e){
                    logger.error("User is not a corporate user ", e);
                }


            }else {

                try {
                    userDetails = retailUserDetailsService.loadUserByUsername(username);
                    logger.info("retail user details authentication " + userDetails);

                    if (userDetails != null) {
                        validate = jwtTokenUtil.validateToken(authToken, userDetails, isCorpUser);
                        logger.info("validate response {} " + validate);
                        if (validate) {
                            grantAccess(userDetails, authToken);
                            logger.info("user %s has been granted access");
                        } else {
                            error = "Token could not be validated as it does not pass data integrity test. Kindly generate another and try again";
                            response.getWriter().write(processErrorMessage(error));
                            return;
                        }
                    }


                } catch (Exception e) {
                    logger.info("User is not a retail user...", e);
                }
            }

        } else {
            error = "Authentication failed - no Bearer token provided.";
        }
        chain.doFilter(request, response);
    }

    private String processErrorMessage(String errorMessage){
        ResponseData responseData =new ResponseData();
        responseData.setCode("99");
        responseData.setMessage(errorMessage);
        responseData.setError(true);
        return responseData.customApiString();
    }

    private Boolean grantAccess(UserDetails userDetails,String authToken){

        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
        authentication.setToken(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("user {} has been granted access" ,userDetails.getUsername());

        return true;
    }
}
