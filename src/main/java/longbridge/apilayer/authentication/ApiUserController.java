package longbridge.apilayer.authentication;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import longbridge.apilayer.models.ApiUser;
import longbridge.security.api.JwtTokenUtil;
import longbridge.utils.ApiUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@Api(value = "User Login", description = "Login Credentials", tags = {"User Login"})
@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${jwt.header}")
    private String tokenHeader;


    @Autowired
    JwtTokenUtil userUtil;
    @Autowired
    ApiUtil apiUtil;

    @ApiOperation(value = "Sign in", tags = {"User Login"})
    @PostMapping(value = "/signin")
    public Object Signin(@RequestBody ApiUser passedUser){
        return apiUtil.validateUser(passedUser);
    }

    @ApiOperation(value = "Sign in", tags = {"User Login"})
    @PostMapping(value = "/signin/encrypted")
    public Object encrypted(@RequestBody ApiUser passedUser){

        return apiUtil.validateUserUsingEncryptedPass(passedUser);
    }
}
