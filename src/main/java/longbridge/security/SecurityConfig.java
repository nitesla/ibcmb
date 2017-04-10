//package longbridge.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
///**
// * Created by Wunmi on 27/03/2017.
// */
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private LtUserDetailsService ltUserDetailsService;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/", "/static/**").permitAll()
//                //.antMatchers("/trade/**").hasIpAddress("172.20.141.0/24")
//                //.antMatchers("/dashboard").hasAuthority("ADMIN")
//                .anyRequest().fullyAuthenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .failureUrl("/login?error")
//                .usernameParameter("email")
//                .defaultSuccessUrl("/dashboard")
//                .permitAll()
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/login")
//                .permitAll();
//    }
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                //.inMemoryAuthentication().withUser("email").password("password").roles("roles");
//                .userDetailsService(ltUserDetailsService)
//                .passwordEncoder(new BCryptPasswordEncoder());
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers("/resources/**", "/static/**", "/css/**", "/font-awesome/**", "/fonts/**",
//                        "/js/**", "/img/**", "/home/**", "/error/**", "/api/**");
//    }
//}