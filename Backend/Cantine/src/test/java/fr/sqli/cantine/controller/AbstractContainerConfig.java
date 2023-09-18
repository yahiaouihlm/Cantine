package fr.sqli.cantine.controller;

import com.auth0.jwt.JWT;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Date;
import java.util.stream.Collectors;

public class AbstractContainerConfig {

    private final  String exceptionMessage = "exceptionMessage";
    private   final   String  responseMessage = "message";

    static   final PostgreSQLContainer postgreSQLContainer ;
    static {
            postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("cantine_tests")
                .withUsername("postgres")
                .withPassword("halim");
            postgreSQLContainer.start();
    }

    /**
     *  return   String  witch contains exception message combined with right  key= "exceptionMessage" as json format
     * @param exceptionMessage message to be combined with key "exceptionMessage"
     * @return String witch contains exception message combined with right  key= "exceptionMessage" as json format
     * @throws JSONException if the key is not valid.
     */
    protected  String  exceptionMessage(String exceptionMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (exceptionMessage==null) {
            jsonObject.put(this.exceptionMessage, "Exception message is null in Method AbstractContainerConfig.exceptionMessage");
        } else {
            jsonObject.put(this.exceptionMessage, exceptionMessage);
        }
        return jsonObject.toString();

    }


    protected  String  responseMessage(String responseMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (responseMessage==null) {
            jsonObject.put(this.responseMessage, "Response message is null in Method AbstractContainerConfig.responseMessage");
        } else {
            jsonObject.put(this.responseMessage, responseMessage);
        }
        return jsonObject.toString();

    }


    protected  String getAuthenticationToken  ( String username )  {

        /*String jwtAccessToken  = JWT.create()
                .withSubject(authResult.getName())   //  600=> 60 (in first of application)     ('+5 * 6000 *1000')
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 6000 *10000))
                .withIssuer(request.getRequestURI())
                .withClaim("roles" , authResult.getAuthorities().stream().map(GrantedAuthority:: getAuthority).collect(Collectors.toList()))
                .sign(algorithm);*/

        return null ;
    }
}
