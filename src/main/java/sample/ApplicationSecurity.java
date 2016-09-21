package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private RESTAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RESTAuthenticationFailureHandler restAuthenticationFailureHandler;
    @Autowired
    private RESTAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    @Autowired
    private RESTLogoutSuccessHandler restLogoutSuccessHandler;

    @Autowired
    @Qualifier("customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(customUserDetailsService);
        builder.authenticationProvider(authenticationProvider());
//        builder.inMemoryAuthentication().withUser("user").password("user").roles("USER").and().withUser("admin")
//                .password("admin").roles("ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        authenticationProvider.setSaltSource(saltSource());
        return authenticationProvider;
    }

    @Bean
    public SaltSource saltSource() throws Exception {
        ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("salt");
        saltSource.afterPropertiesSet();
        return saltSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/rest/**").authenticated();
        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().loginProcessingUrl("/login").successHandler(restAuthenticationSuccessHandler);
        http.formLogin().failureHandler(restAuthenticationFailureHandler);
        http.logout().logoutUrl("/logout").logoutSuccessHandler(restLogoutSuccessHandler);
//        logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
        // CSRF tokens handling
        http.csrf().disable();
        http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter()
            throws Exception {
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        customUsernamePasswordAuthenticationFilter.setUsernameParameter("username");
        customUsernamePasswordAuthenticationFilter.setPasswordParameter("password");
        return customUsernamePasswordAuthenticationFilter;
    }
}