package fr.sqli.cantine.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Date;
import java.util.stream.Collectors;
@AutoConfigureMockMvc
@SpringBootTest
/*TODO CHECK  LE  COMMENT BELLOW */
//@Transactional @Rollback  ==>  supprimer les mofications apportées par les tests dans la base de données et la rendre  telqu'elle était avant les tests

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
