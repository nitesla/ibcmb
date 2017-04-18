
package longbridge.config;

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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.http.SessionCreationPolicy;


/**
 * Created by ayoade_farooq@yahoo.com on 4/10/2017.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    public void customConfig(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }


    @Configuration
    @Order(1)
    public static class AdminUserConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        @Qualifier("adminUserDetails")
        UserDetailsService adminDetails;
        @Autowired
        BCryptPasswordEncoder bCryptPasswordEncoder;


        public AdminUserConfigurationAdapter() {
            super();
        }

//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth
//                    .userDetailsService(adminDetails).passwordEncoder(bCryptPasswordEncoder);
//        }
   @Override
     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
    }
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http

                    .antMatcher("/admin/**").authorizeRequests()
                    .anyRequest().hasRole("ADMIN")
                    // log in
                    .and().formLogin().loginPage("/loginAdmin").loginProcessingUrl("/admin/login").failureUrl("/loginAdmin?error=loginError").defaultSuccessUrl("/dashboard")


                    .and()
                    // logout
                    .logout().logoutUrl("/admin_logout").logoutSuccessUrl("/protectedLinks").deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and().csrf().disable()
                    .sessionManagement()
                   .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/loginAdmin")
                    .maximumSessions(1)
                    .expiredUrl("/loginAdmin");


        }

        @Override
        public void configure(WebSecurity web) throws Exception {
           new SecurityConfig(). customConfig(web);
        }

    }






    //#########################OPERATIONS USER SECURITY CONFIGURATION########################################


    @Configuration
    @Order(2)
    public static class OperationsUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("operationUserDetails")
        UserDetailsService opsDetails;
        @Autowired
        BCryptPasswordEncoder bCryptPasswordEncoder;
        public OperationsUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(opsDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/operations/**").authorizeRequests().anyRequest().hasRole("RETAIL")

                    // log in
                    .and().formLogin().loginPage("/loginOps").loginProcessingUrl("/operations/login").failureUrl("/loginOps?error=true").defaultSuccessUrl("/opsPage")//TODO LANDING PAGE
                    .and()


                    // logout
                    .logout().logoutUrl("/ops_logout").logoutSuccessUrl("/loginOps").deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and().csrf().disable()

                   .sessionManagement()
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/loginOps")
                    .maximumSessions(1)
                    .expiredUrl("/loginOps");
        }
        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig(). customConfig(web);
        }
    }



    //#########################RETAIL USER SECURITY CONFIGURATION########################################


    @Configuration
    @Order(3)
    public static class RetailUserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("retailUserDetails")
        UserDetailsService retDetails;
        @Autowired
        BCryptPasswordEncoder bCryptPasswordEncoder;
        public RetailUserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(retDetails).passwordEncoder(bCryptPasswordEncoder);
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/user/**").authorizeRequests().anyRequest().hasRole("RETAIL")
                    // log in
                    .and().formLogin().loginPage("/login").loginProcessingUrl("/user/login").failureUrl("/loginUser?error=true").defaultSuccessUrl("/userPage")
                    .and()

                    // logout
                   .logout().logoutUrl("/user_logout").logoutSuccessUrl("/protectedLinks").deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and().csrf().disable()

                    .sessionManagement()
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login")
                    .maximumSessions(1)
                    .expiredUrl("/login");

        }
        @Override
        public void configure(WebSecurity web) throws Exception {
            new SecurityConfig(). customConfig(web);
        }

    }


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}

