package longbridge.config;

import longbridge.dtos.SettingDTO;
import longbridge.models.UserType;
import longbridge.security.CustomerInternetBankingPassWordEncoder;
import longbridge.security.adminuser.AdminAuthenticationSuccessHandler;
import longbridge.security.api.ApiAuthenticationFilter;
import longbridge.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;
import java.util.Objects;


@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {


    @Autowired
    public HttpSessionEventPublisher httpSessionEventPublisher;

    public void customConfig(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/bank/**", "/resources/**", "/static/**", "/css/** ", "/js/**", "/images/**", "/customer/**");
    }

    @Configuration
    @Order(1)
    public static class AdminUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        SessionRegistry sessionRegistry;
        @Autowired
        @Qualifier("adminUserDetails")
        private UserDetailsService adminDetails;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private AdminAuthenticationSuccessHandler adminAuthenticationSuccessHandler;
        @Autowired
        @Qualifier("adminAuthenticationFailureHandler")
        private AuthenticationFailureHandler adminAuthenticationFailureHandler;
        @Autowired
        private ConfigurationService configService;
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        public AdminUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(adminDetails).passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {


            boolean ipRestricted = false;
            StringBuilder ipRange = new StringBuilder("hasIpAddress('::1') or hasIpAddress('127.0.0.1') ");
            //Takes a specific IP address or a range using
            //the IP/Netmask (e.g. 192.168.1.0/24 or 202.24.0.0/14).
            SettingDTO dto = configService.getSettingByName("ADMIN_IP_WHITELIST");
            if (dto != null && dto.isEnabled()) {
                ipRestricted = true;
                String temp = dto.getValue();
                try {
                    String[] whitelisted = temp.split(",");
                    Arrays.stream(whitelisted)
                            .filter(StringUtils::isNoneBlank)
                            .forEach(i -> ipRange.append(String.format(" or hasIpAddress('%s')", i)));


                } catch (Exception e) {
                    e.printStackTrace();
                }


                logger.info("IP address whitelist {}", ipRange.toString());
            }


            http.authorizeRequests()
                    .antMatchers("/assets/**", "/bank/**", "/customer/**")
                    .permitAll()
                    .antMatchers("/admin/new-pword", "/admin/users/password/new").permitAll()
                    .and()
                    .antMatcher("/admin/**").authorizeRequests()
                    .anyRequest().access("hasAuthority('" + UserType.ADMIN.toString() + "') and  " + (ipRestricted ? ipRange.toString() : "hasIpAddress('::1') or hasIpAddress('127.0.0.1') or hasIpAddress('0.0.0.0/0') "))
                    .and()
                    .formLogin()
                    .failureHandler(adminAuthenticationFailureHandler)
                    .loginPage("/admin/login").permitAll().successHandler(adminAuthenticationSuccessHandler).and().logout()
                    .permitAll().logoutUrl("/admin/logout")
                    .logoutSuccessUrl("/login/admin")
                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/login/admin")
                    .maximumSessions(1)
                    .expiredUrl("/login/admin?expired=true")
                    .sessionRegistry(sessionRegistry);
            http.csrf().disable();


            // disable page caching
            http.headers().cacheControl();
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
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        @Autowired
        private SessionRegistry sessionRegistry;

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
                ipRestricted = true;
                String temp = dto.getValue();
                try {
                    String[] whitelisted = temp.split(",");
                    Arrays.stream(whitelisted)
                            .filter(Objects::nonNull)
                            .forEach(i -> ipRange.append(String.format(" or hasIpAddress('%s')", i)));


                } catch (Exception e) {
                    e.printStackTrace();
                }

                logger.info("IP address whitelist " + ipRange.toString());
            }


            http.authorizeRequests()
                    .antMatchers("/assets/**", "/bank/**", "/customer/**")
                    .permitAll()
                    .antMatchers("/ops/new-pword", "/ops/users/password/new").permitAll()
                    .and()
                    .antMatcher("/ops/**").authorizeRequests()
                    .anyRequest().access("hasAuthority('" + UserType.OPERATIONS.toString() + "') and  " + (ipRestricted ? ipRange.toString() : "hasIpAddress('::1') or hasIpAddress('127.0.0.1') or hasIpAddress('0.0.0.0/0') "))
                    .and()
                    .formLogin()
                    .failureHandler(opAuthenticationFailureHandler)
                    .loginPage("/ops/login").permitAll().successHandler(opAuthenticationSuccessHandler).and().logout()
                    .permitAll().logoutUrl("/ops/logout")
                    .logoutSuccessUrl("/ops/login")
                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/login/ops")
                    .maximumSessions(1)
                    .expiredUrl("/login/ops?expired=true")
                    .sessionRegistry(sessionRegistry);
            http.csrf().disable();
            // disable page caching
            http.headers().cacheControl();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);
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
        @Autowired
        private SessionRegistry sessionRegistry;

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
                    .expiredUrl("/login/retail?expired=true").sessionRegistry(sessionRegistry).and()
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login/retail")
                    .and()

                    // logout

                    // Enable this for only Coronation Bank
                    .logout().logoutUrl("/retail/logout").logoutSuccessUrl("/login/retail/feedback").deleteCookies("JSESSIONID").invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable()

                    // Enable this for other banks
                    //.logout().logoutUrl("/retail/logout").logoutSuccessUrl("/login/retail").deleteCookies("JSESSIONID").invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable()

            ;
            // disable page caching
            http.headers().cacheControl();
        }

        @Bean
        public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
            StrictHttpFirewall firewall = new StrictHttpFirewall();
            firewall.setAllowUrlEncodedSlash(true);
            firewall.setAllowSemicolon(true);
            return firewall;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);
            web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
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
        AuthenticationSuccessHandler corpAuthenticationSuccessHandler;
        @Autowired
        @Qualifier("corporateAuthenticationFailureHandler")
        AuthenticationFailureHandler corpAuthenticationFailureHandler;
        @Autowired
        private SessionRegistry sessionRegistry;

        public CorpUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(corpDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        protected void configure(HttpSecurity http) throws Exception {


//            http.addFilterBefore(customFilter(), UsernamePasswordAuthenticationFilter.class);

            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = http
                    .antMatcher("/corporate/**")
                    .authorizeRequests()
                    .anyRequest();
            authorizedUrl.fullyAuthenticated();

            // .authenticated()
            authorizedUrl.hasAuthority(UserType.CORPORATE.toString())
                    .and().authorizeRequests()

                    // log in
                    .and().formLogin().loginPage("/login/corporate").loginProcessingUrl("/corporate/login")

                    .failureUrl("/login/corporate?error=true").defaultSuccessUrl("/corporate/dashboard")
                    .successHandler(corpAuthenticationSuccessHandler)
                    .failureHandler(corpAuthenticationFailureHandler)


                    //.failureForwardUrl()


                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/login/corporate")
                    .maximumSessions(1).expiredUrl("/login/corporate?expired=true")
                    .sessionRegistry(sessionRegistry).and()
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    // logout

                    // Enable this for only Coronation Bank
                    .logout().logoutUrl("/corporate/logout").logoutSuccessUrl("/login/corporate/feedback").deleteCookies("JSESSIONID").invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable();

                    // Enable this for other banks
                    //.logout().logoutUrl("/corporate/logout").logoutSuccessUrl("/login/corporate").deleteCookies("JSESSIONID").invalidateHttpSession(true).and().exceptionHandling().and().csrf().disable();


            // disable page caching
            http.headers().cacheControl();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig().customConfig(web);
        }


//        @Bean
//        public CorperateAuthenticationFilter customFilter() throws Exception {
//            CorperateAuthenticationFilter customFilter = new CorperateAuthenticationFilter();
//            customFilter.setAuthenticationManager(authenticationManagerBean());
//            customFilter.setAuthenticationSuccessHandler(corpAuthenticationSuccessHandler);
//            customFilter.setAuthenticationFailureHandler(corpAuthenticationFailureHandler);
//            return customFilter;
//        }


    }


    @Configuration
    @Order(5)
    public static class ApiUserConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("apiUserDetails")
        UserDetailsService apiUser;

        @Autowired
        @Qualifier("bCryptPasswordEncoder")
        CustomerInternetBankingPassWordEncoder bCryptPasswordEncoder;

        @Bean
        public ApiAuthenticationFilter apiAuthenticationTokenFilter() throws Exception {
            return new ApiAuthenticationFilter();
        }

        //
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(apiUser).passwordEncoder(bCryptPasswordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {


//            http.addFilterBefore(apiAuthenticationTokenFilter());

            http.addFilterBefore(apiAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

            http
                    .antMatcher("/api/v1/**").authorizeRequests()
                    .anyRequest()
                    .fullyAuthenticated()
                    .and()
                    .exceptionHandling().and().csrf().disable();

            // disable page caching
        }


    }

}