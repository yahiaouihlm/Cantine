package fr.sqli.cantine.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.cantine.security.exceptionHandler.CustomAccessDeniedHandler;
import fr.sqli.cantine.security.exceptionHandler.CustomAuthenticationEntryPoint;
import fr.sqli.cantine.security.exceptionHandler.StoneAuthenticationFailureHandler;
import fr.sqli.cantine.security.jwt.JwtTokenVerifier;
import fr.sqli.cantine.security.jwt.JwtUsernameAndPasswordAuthenticationFiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    private AppUserService appUserService;
    private JwtTokenVerifier jwtTokenVerifier;
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    private StoneAuthenticationFailureHandler stoneAuthenticationFailureHandler = new StoneAuthenticationFailureHandler();
    private ObjectMapper objectMapper;

    public SecurityConfig(AppUserService appUserService, CustomAccessDeniedHandler customAccessDeniedHandler,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenVerifier jwtTokenVerifier) {

        this.appUserService = appUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenVerifier = jwtTokenVerifier;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;

    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeRequests(
                        authorize -> {
                            authorize.requestMatchers("cantine/api/**").permitAll();
                            authorize.requestMatchers("/cantine/superAdmin/ExistingEmail").permitAll();
                            authorize.requestMatchers("/cantine/download/images/**",
                                    "/cantine/download/images/**").permitAll();
                            authorize.requestMatchers("/cantine/admin/getAllAdminFunctions"
                                    ,"/cantine/user/check-confirmation-token/**"
                                    ,"/cantine/user/existing-email"
                                    ,"/cantine/user/send-reset-password-link/**"
                                    , "/cantine/admin/adminDashboard/signUp").permitAll();
                            authorize.requestMatchers("/cantine/admin/adminDashboard/getAllAdminFunctions").permitAll();
                            authorize.requestMatchers("/cantine/student/getAllStudentClass"
                                    , "/cantine/student/signUp"
                                    , "/cantine/user/send-confirmation-link"
                                    , "/cantine/user/reset-password/**"
                            ).permitAll();
                            authorize.anyRequest().authenticated();
                        })
                .authenticationProvider(authenticationProvider())
                .addFilter(new JwtUsernameAndPasswordAuthenticationFiler(authenticationManager()))
                .addFilterBefore(jwtTokenVerifier, JwtUsernameAndPasswordAuthenticationFiler.class)
                .exceptionHandling()
                .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                .accessDeniedHandler(this.stoneAuthenticationFailureHandler)
                .and()
                .build();

    }
/*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization" , "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

*/


    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }

/*
    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }*/
}
