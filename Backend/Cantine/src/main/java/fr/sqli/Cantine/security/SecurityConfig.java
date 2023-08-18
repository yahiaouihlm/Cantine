package fr.sqli.Cantine.security;

import fr.sqli.Cantine.security.exceptionHandler.CustomAccessDeniedHandler;
import fr.sqli.Cantine.security.exceptionHandler.CustomAuthenticationEntryPoint;
import fr.sqli.Cantine.security.exceptionHandler.StoneAuthenticationFailureHandler;
import fr.sqli.Cantine.security.jwt.JwtUsernameAndPasswordAuthenticationFiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AppUserService   appUserService ;

    private CustomAuthenticationEntryPoint  customAuthenticationEntryPoint ;
    private CustomAccessDeniedHandler customAccessDeniedHandler ;
    private StoneAuthenticationFailureHandler stoneAuthenticationFailureHandler = new  StoneAuthenticationFailureHandler() ;
    public SecurityConfig (AppUserService appUserService ,CustomAccessDeniedHandler customAccessDeniedHandler ,  CustomAuthenticationEntryPoint  customAuthenticationEntryPoint   , BCryptPasswordEncoder bCryptPasswordEncoder){
        this.appUserService = appUserService ;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint ;
        this.customAccessDeniedHandler = customAccessDeniedHandler ;
      //  this.stoneAuthenticationFailureHandler = stoneAuthenticationFailureHandler ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http   ) throws Exception {

        return  http
                .csrf( csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeRequests(
                        authorize -> {
                    authorize.requestMatchers("/public").permitAll();
                    authorize.anyRequest().authenticated();
                        })
                .addFilter( new JwtUsernameAndPasswordAuthenticationFiler(authenticationManager()))
                .exceptionHandling()
                .authenticationEntryPoint(this.customAuthenticationEntryPoint)
              //  .accessDeniedHandler(this.stoneAuthenticationFailureHandler)
                .and()
                .httpBasic()
                .and()
               .build();

    }



    @Bean
    public AuthenticationManager authenticationManager(){
        return   new ProviderManager(authenticationProvider());
    }

    @Bean
    AuthenticationProvider authenticationProvider () {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return   provider ;
    }

}
