package fr.sqli.cantine.security.jwt;


import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.cantine.constants.ConstCantine;
import fr.sqli.cantine.service.users.user.impl.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.stream;


@Component
public class JwtTokenVerifier extends OncePerRequestFilter {


    private final Environment environment;
    private final UserService userService;


    @Autowired
    public JwtTokenVerifier(UserService userService, Environment environment) {
        this.userService = userService;
        this.environment = environment;

    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (request.getServletPath().equals("/user/login")) {
            filterChain.doFilter(request, response);
        } else {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                String token = authorizationHeader.replace("Bearer ", "");
                try {
                    String secretKey = this.environment.getProperty("sqli.cantine.jwt.secret");
                    assert secretKey != null;
                    Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    var user = this.userService.findUser(username);

                    if (user.getRoles().stream().anyMatch(role -> role.getLabel().equals(ConstCantine.ADMIN_ROLE_LABEL))) {
                        if (user.getStatus() != 1 || user.getValidation() != 1) {
                            throw new Exception("ACCOUNT NOT ACTIVATED");
                        }
                    } else if (user.getRoles().stream().anyMatch(role -> role.getLabel().equals(ConstCantine.STUDENT_ROLE_LABEL))) {
                        if (user.getStatus() != 1) {
                            throw new Exception("ACCOUNT NOT ACTIVATED");
                        }
                    } else {
                        throw new Exception("ACCOUNT NOT ACTIVATED");
                    }
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);


                } catch (Exception e) {
                    response.addHeader("error", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    Map<String, String> error = new HashMap<>();
                    error.put("exceptionMessage", "EXPIRED_TOKEN");
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }

            } else {
                filterChain.doFilter(request, response);
            }
        }

    }


}