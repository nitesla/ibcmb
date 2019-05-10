package longbridge.security;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component("bCryptPasswordEncoder")
public class CustomerInternetBankingPassWordEncoder extends BCryptPasswordEncoder {
}
