package fr.sqli.Cantine.controller.admin;

import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractMealTest {

    protected  final  String BASE_MEAL_URL  =  "/cantine/api/admin/meals";
    protected final  String GET_ALL_MEALS_URL  =  BASE_MEAL_URL  +  "/getAll";

    protected   final   String ADD_MEAL_URL  =  BASE_MEAL_URL  +  "/add";

    static   final PostgreSQLContainer    postgreSQLContainer ;
    static {
        postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("cantine_tests")
                .withUsername("postgres")
                .withPassword("halim");
        postgreSQLContainer.start();
    }

}
