package fr.sqli.cantine.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.testcontainers.containers.PostgreSQLContainer;

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


}
