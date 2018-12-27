package longbridge.config;

import longbridge.dtos.SettingDTO;
import longbridge.models.UserType;
import longbridge.security.adminuser.AdminAuthenticationSuccessHandler;
import longbridge.security.corpuser.CorperateAuthenticationFilter;
import longbridge.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by ayoade_farooq@yahoo.com on 4/10/2017.
 */


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    public void customConfig(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources *", "/static *", "/css ", "/js *", "/images *", "/customer");
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Configuration
    @Order(1)
    public static class AdminUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("adminUserDetails")
        private UserDetailsService adminDetails;
        @Autowired
        private BCryptPasswordEncoder bCryptPasswordEncoder;
        @Autowired
        private AdminAuthenticationSuccessHandler adminAuthenticationSuccessHandler;
        @Autowired
        @Qualifier("adminAuthenticationFailureHandler")
        private AuthenticationFailureHandler adminAuthenticationFailureHandler;
        @Autowired
        private ConfigurationService configService;
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public AdminUserConfigurationAdapter() {
            super();

        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(adminDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {


            boolean ipRestricted = false;
            StringBuilder ipRange = new StringBuilder("hasIpAddress('::1') or hasIpAddress('127.0.0.1')");
            //Takes a specific IP address or a range using
            //the IP/Netmask (e.g. 192.168.1.0/24 or 202.24.0.0/14).
            SettingDTO dto = configService.getSettingByName("ADMIN_IP_WHITELIST");
            if (dto != null && dto.isEnabled()) {
                ipRestricted = true;
                String temp = dto.getValue();
                try {
                    String[] whitelisted = temp.split(",");
                    Arrays.asList(whitelisted)
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(i -> ipRange.append(String.format(" or hasIpAddress('%s')", i)));
logger.info("the ip whitelist {}",whitelisted);

                } catch (Exception e) {
                    e.printStackTrace();
                }



                logger.info("IP address whitelist " + ipRange.toString());
            }


            http.antMatcher("/admin/**").authorizeRequests()
                    .anyRequest().fullyAuthenticated()
                    .and().antMatcher("/**").authorizeRequests()
                    .and().antMatcher("/admin/**").authorizeRequests()
                    .and().authorizeRequests().anyRequest().
                    //access("hasAuthority('" + UserType.ADMIN.toString() + "')").and()
                    access("hasAuthority('" + UserType.ADMIN.toString() + "') and " + ipRange.toString()).and()

                    .formLogin().loginPage("/login/admin").loginProcessingUrl("/admin/login")
                    .failureUrl("/login/admin?error=login_error").defaultSuccessUrl("/admin/dashboard")
                    .successHandler(adminAuthenticationSuccessHandler).failureHandler(adminAuthenticationFailureHandler)
                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/login/admin")
                    .maximumSessions(1)
                    .expiredUrl("/login/admin?expired=true")
                    .sessionRegistry(sessionRegistry()).and()

                    .sessionFixation().migrateSession().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                    .and()
                    .logout().logoutUrl("/admin/logout").logoutSuccessUrl("/login/admin").deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .and().exceptionHandling().and().csrf().disable();


            // disable page caching
            http.headers().cacheControl();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);


        }

    }


    // #########################RETAIL USER SECURITY
    // CONFIGURATION########################################

    //#########################OPERATIONS USER SECURITY CONFIGURATION########################################
    @Configuration
    @Order(2)
    public static class OperationsUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("operationUserDetails")
        private UserDetailsService opsDetails;
        @Autowired
        private BCryptPasswordEncoder bCryptPasswordEncoder;
        @Autowired
        @Qualifier("opAuthenticationSuccessHandler")
        private AuthenticationSuccessHandler opAuthenticationSuccessHandler;
        @Autowired
        @Qualifier("opAuthenticationFailureHandler")
        private AuthenticationFailureHandler opAuthenticationFailureHandler;
        @Autowired
        private ConfigurationService configService;
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public OperationsUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(opsDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        protected void configure(HttpSecurity http) throws Exception {


            boolean ipRestricted = false;
            StringBuilder ipRange = new StringBuilder("hasIpAddress('::1') or hasIpAddress('127.0.0.1')");
            //Takes a specific IP address or a range using
            //the IP/Netmask (e.g. 192.168.1.0/24 or 202.24.0.0/14).
            SettingDTO dto = configService.getSettingByName("ADMIN_IP_WHITELIST");
            if (dto != null && dto.isEnabled()) {

                String temp = dto.getValue();
                try {
                    String[] whitelisted = temp.split(",");
                    Arrays.asList(whitelisted)
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(i -> ipRange.append(String.format(" or hasIpAddress('%s')", i)));


                } catch (Exception e) {
                    e.printStackTrace();
                }


                logger.info("IP address whitelist " + ipRange.toString());
            }
            http.antMatcher("/ops/**")
                    .authorizeRequests().anyRequest()


                    //.authenticated()
                    // .hasAuthority(UserType.OPERATIONS.toString())
                    .fullyAuthenticated().anyRequest()
//                    .access("hasAuthority('" + UserType.OPERATIONS.toString() + "')").and()
                    .access("hasAuthority('" + UserType.OPERATIONS.toString() + "') and " + ipRange.toString()).and()
                    // log in
                    .formLogin().loginPage("/login/ops").loginProcessingUrl("/ops/login").failureUrl("/login/ops?error=true").defaultSuccessUrl("/ops/dashboard")
                    .successHandler(opAuthenticationSuccessHandler)
                    .failureHandler(opAuthenticationFailureHandler)

                    .and()
                    .sessionManagement()

                    .invalidSessionUrl("/login/ops")
                    .maximumSessions(1)
                    .expiredUrl("/login/ops?expired=true")
                    .sessionRegistry(sessionRegistry()).and()

                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)


                    .and()
                    // logout
                    .logout().logoutUrl("/ops/logout")
                    .logoutSuccessUrl("/login/ops").deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable()

            ;
            // disable page caching
            http.headers().cacheControl();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);
        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }
    }

    @Configuration
    @Order(3)
    public static class RetailUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("retailUserDetails")
        UserDetailsService retDetails;
        @Autowired
        BCryptPasswordEncoder bCryptPasswordEncoder;
        @Autowired
        @Qualifier("retailAuthenticationSuccessHandler")
        AuthenticationSuccessHandler retailAuthenticationSuccessHandler;
        @Autowired
        @Qualifier("retailAuthenticationFailureHandler")
        private AuthenticationFailureHandler retailAuthenticationFailureHandler;

        public RetailUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(retDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        protected void configure(HttpSecurity http) throws Exception {

            http
                    .antMatcher("/retail/**").authorizeRequests()
                    .anyRequest()
                    // .authenticated()
                    // .hasAuthority(UserType.RETAIL.toString())
                    .fullyAuthenticated()
                    // log in
                    .and().formLogin().loginPage("/login/retail").loginProcessingUrl("/retail/login")
                    .failureUrl("/login/retail?error=true").defaultSuccessUrl("/retail/dashboard")
                    //.successHandler(retailAuthenticationSuccessHandler)
                    .successHandler(retailAuthenticationSuccessHandler)
                    .failureHandler(retailAuthenticationFailureHandler)

                    //.failureForwardUrl()
                    .and()
                    .sessionManagement()

                    .invalidSessionUrl("/login/retail")
                    .maximumSessions(1)
                    .expiredUrl("/login/retail?expired=true").sessionRegistry(sessionRegistry()).and()
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login/retail")
                    .and()

                    // logout
                    .logout().logoutUrl("/retail/logout").logoutSuccessUrl("/login/retail").deleteCookies("JSESSIONID").invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable()

            ;
            // disable page caching
            http.headers().cacheControl();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);
        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
            return new HttpSessionEventPublisher();
        }

    }

    @Configuration
    @Order(4)
    public static class CorpUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("corpUserDetails")
        UserDetailsService corpDetails;
        @Autowired
        BCryptPasswordEncoder bCryptPasswordEncoder;
        @Autowired
        @Qualifier("corporateAuthenticationSuccessHandler")
        private AuthenticationSuccessHandler corpAuthenticationSuccessHandler;
        @Autowired
        @Qualifier("corporateAuthenticationFailureHandler")
        private AuthenticationFailureHandler corpAuthenticationFailureHandler;

        public CorpUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(corpDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        protected void configure(HttpSecurity http) throws Exception {


//            http.addFilterBefore(customFilter(), UsernamePasswordAuthenticationFilter.class);

            http
                    .antMatcher("/corporate/**").authorizeRequests()
                    .anyRequest()

                    // .authenticated()
                    .hasAuthority(UserType.CORPORATE.toString())
                    .anyRequest().fullyAuthenticated()
                    .anyRequest().authenticated()

                    // log in
                    .and().formLogin().loginPage("/login/corporate").loginProcessingUrl("/corporate/login")

                    .failureUrl("/login/corporate?error=true").defaultSuccessUrl("/corporate/dashboard")
                    .successHandler(corpAuthenticationSuccessHandler)
                    .failureHandler(corpAuthenticationFailureHandler)


                    //.failureForwardUrl()

                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/login/corporate")
                    .maximumSessions(1).expiredUrl("/login/corporate?expired=true").sessionRegistry(sessionRegistry()).and()
                    .sessionFixation().migrateSession()
                    .and()
                    // logout
                    .logout().logoutUrl("/corporate/logout").logoutSuccessUrl("/login/corporate").deleteCookies("JSESSIONID").invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable();


            // disable page caching
            http.headers().cacheControl();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);
        }


        @Bean
        public CorperateAuthenticationFilter customFilter() throws Exception {
            CorperateAuthenticationFilter customFilter = new CorperateAuthenticationFilter();
            customFilter.setAuthenticationManager(authenticationManagerBean());
            customFilter.setAuthenticationSuccessHandler(corpAuthenticationSuccessHandler);
            customFilter.setAuthenticationFailureHandler(corpAuthenticationFailureHandler);
            return customFilter;
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
            return new HttpSessionEventPublisher();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

    }


}
