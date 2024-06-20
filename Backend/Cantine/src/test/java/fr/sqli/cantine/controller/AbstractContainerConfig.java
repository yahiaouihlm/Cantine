package fr.sqli.cantine.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@AutoConfigureMockMvc
@SpringBootTest
/*TODO CHECK  LE  COMMENT BELLOW */
//@Transactional @Rollback  ==>  supprimer les mofications apportées par les tests dans la base de données et la rendre  telqu'elle était avant les tests

public class AbstractContainerConfig {

    private final String exceptionMessage = "exceptionMessage";
    private final String responseMessage = "message";

    static final PostgreSQLContainer postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("cantine_tests")
                .withUsername("postgres")
                .withPassword("halim");
        postgreSQLContainer.start();
    }


    /**
     * return   String  witch contains exception message combined with right  key= "exceptionMessage" as json format
     *
     * @param exceptionMessage message to be combined with key "exceptionMessage"
     * @return String witch contains exception message combined with right  key= "exceptionMessage" as json format
     * @throws JSONException if the key is not valid.
     */
    protected String exceptionMessage(String exceptionMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(this.exceptionMessage, Objects.requireNonNullElse(exceptionMessage, "Exception message is null in Method AbstractContainerConfig.exceptionMessage"));
        return jsonObject.toString();

    }

    protected String responseMessage(String responseMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(this.responseMessage, Objects.requireNonNullElse(responseMessage, "Response message is null in Method AbstractContainerConfig.responseMessage"));
        return jsonObject.toString();

    }


}
