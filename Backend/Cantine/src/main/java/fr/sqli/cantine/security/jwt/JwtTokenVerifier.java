package fr.sqli.cantine.security.jwt;


import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.cantine.constants.Messages;
import fr.sqli.cantine.service.users.admin.impl.AdminService;
import fr.sqli.cantine.service.users.student.Impl.StudentService;
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
    private final AdminService adminService;
    private final StudentService studentService;


    @Autowired
    public JwtTokenVerifier(Environment environment, AdminService adminService, StudentService studentService) {
        this.environment = environment;
        this.adminService = adminService;
        this.studentService = studentService;

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
                    if (Arrays.asList(roles).contains(Messages.ADMIN_ROLE)) {
                        var admin = this.adminService.findByUsername(username);
                        if (admin.getStatus() != 1 || admin.getValidation() != 1) {
                            throw new Exception("Account not activated");
                        }
                    }
                    else if (Arrays.asList(roles).contains(Messages.STUDENT_ROLE)) {
                        var student = this.studentService.findStudentByUserName(username);
                        if (student.getStatus() != 1) {
                            throw new Exception("Account not activated");
                        }
                    } else {
                        throw new Exception("Invalid role");
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