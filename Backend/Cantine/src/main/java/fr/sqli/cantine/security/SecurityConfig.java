package fr.sqli.cantine.security;


import fr.sqli.cantine.security.exceptionHandler.CustomAccessDeniedHandler;
import fr.sqli.cantine.security.exceptionHandler.CustomAuthenticationEntryPoint;
import fr.sqli.cantine.security.exceptionHandler.StoneAuthenticationFailureHandler;
import fr.sqli.cantine.security.jwt.JwtTokenVerifier;
import fr.sqli.cantine.security.jwt.JwtUsernameAndPasswordAuthenticationFiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserService appUserService;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final StoneAuthenticationFailureHandler stoneAuthenticationFailureHandler = new StoneAuthenticationFailureHandler();
    private final Environment environment;
    private final String FRONT_END_URL;
    private final String MOBILE_FRONT_URL;

    public SecurityConfig(AppUserService appUserService, CustomAccessDeniedHandler customAccessDeniedHandler, Environment environment,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenVerifier jwtTokenVerifier) {

        this.appUserService = appUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenVerifier = jwtTokenVerifier;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.environment = environment;

        this.FRONT_END_URL = environment.getProperty("sqli.cantine.server.protocol") +
                  environment.getProperty("sqli.cantine.server.ip.address") +
                ":" + environment.getProperty("sali.cantine.server.port");

        this.MOBILE_FRONT_URL = environment.getProperty("sqli.cantine.server.protocol") +
                environment.getProperty("sqli.cantine.server.ip.address") +
                ":" + environment.getProperty("sqli.canine.server.port.mobile");
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeRequests(
                        authorize -> {
                            authorize
                                    .requestMatchers("/cantine/api/getAll/**", "/cantine/api/getAll/getMealsByType/**").permitAll();

                            authorize
                                    .requestMatchers("/cantine/download/images/**",
                                            "/cantine/download/images/**").permitAll();

                            authorize
                                    .requestMatchers("/cantine/admin/getAllAdminFunctions"
                                            , "/cantine/user/check-confirmation-token/**"
                                            , "/cantine/user/existing-email"
                                            , "/cantine/user/send-reset-password-link/**"
                                            , "/cantine/admin/register").permitAll();

                            authorize
                                    .requestMatchers("/cantine/user/student/getAllStudentClass"
                                            , "/cantine/user/student/signUp"
                                            , "/cantine/user/send-confirmation-link"
                                            , "/cantine/user/reset-password/**").permitAll();

                            authorize.anyRequest().authenticated();
                        })

                .authenticationProvider(authenticationProvider())
                .addFilter(new JwtUsernameAndPasswordAuthenticationFiler(authenticationManager(), this.environment))
                .addFilterBefore(jwtTokenVerifier, JwtUsernameAndPasswordAuthenticationFiler.class)
                .exceptionHandling()
                .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                .accessDeniedHandler(this.stoneAuthenticationFailureHandler)
                .and()
                .build();

    }

    @Bean
    public UserDetailsChecker customUserDetailsChecker() {
        return new CustomUserDetailsChecker();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(this.FRONT_END_URL ,  this.MOBILE_FRONT_URL));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setPreAuthenticationChecks(customUserDetailsChecker());
        return provider;
    }

}
