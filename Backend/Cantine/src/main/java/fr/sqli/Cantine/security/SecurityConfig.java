package fr.sqli.Cantine.security;

import fr.sqli.Cantine.security.exceptionHandler.CustomAccessDeniedHandler;
import fr.sqli.Cantine.security.exceptionHandler.CustomAuthenticationEntryPoint;
import fr.sqli.Cantine.security.exceptionHandler.StoneAuthenticationFailureHandler;
import fr.sqli.Cantine.security.jwt.JwtTokenVerifier;
import fr.sqli.Cantine.security.jwt.JwtUsernameAndPasswordAuthenticationFiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AppUserService   appUserService ;
    private JwtTokenVerifier jwtTokenVerifier ;
    private CustomAuthenticationEntryPoint  customAuthenticationEntryPoint ;
    private CustomAccessDeniedHandler customAccessDeniedHandler ;
    private StoneAuthenticationFailureHandler stoneAuthenticationFailureHandler = new  StoneAuthenticationFailureHandler() ;
    public SecurityConfig (AppUserService appUserService ,CustomAccessDeniedHandler customAccessDeniedHandler ,
                           CustomAuthenticationEntryPoint  customAuthenticationEntryPoint   , BCryptPasswordEncoder bCryptPasswordEncoder ,  JwtTokenVerifier jwtTokenVerifier){
        this.appUserService = appUserService ;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenVerifier = jwtTokenVerifier ;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint ;
        this.customAccessDeniedHandler = customAccessDeniedHandler ;
      //  this.stoneAuthenticationFailureHandler = stoneAuthenticationFailureHandler ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http   ) throws Exception {

        return  http
                .csrf( csrf -> csrf.disable())
               .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .authorizeRequests(
                        authorize -> {
                            authorize.requestMatchers("cantine/api/**").permitAll();
                            authorize.requestMatchers("/cantine/superAdmin/ExistingEmail").permitAll();
                            authorize.requestMatchers("/cantine/download/images/meals/**").permitAll();
                            authorize.requestMatchers("/cantine/admin/adminDashboard/getAllAdminFunctions",
                                                               "/cantine/admin/adminDashboard/checkTokenValidity"
                                                               , "/cantine/admin/adminDashboard/sendToken/**"
                                                                ,"/cantine/admin/adminDashboard/signUp").permitAll();
                            authorize.requestMatchers("/cantine/admin/adminDashboard/getAllAdminFunctions").permitAll();
                            authorize.requestMatchers("/cantine/student/getAllStudentClass"
                                                              ,"/cantine/student/sendToken"
                                                               ,  "/cantine/student/signUp"
                             ,"/cantine/user/v1/token-sender/send-token"
                            ).permitAll();
                    authorize.anyRequest().authenticated();
                        })
                .addFilter( new JwtUsernameAndPasswordAuthenticationFiler(authenticationManager()))
                .addFilterBefore(jwtTokenVerifier, JwtUsernameAndPasswordAuthenticationFiler.class)
         /*       .exceptionHandling()
                 .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                .accessDeniedHandler(this.stoneAuthenticationFailureHandler)*/

               .build();

    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization" , "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
