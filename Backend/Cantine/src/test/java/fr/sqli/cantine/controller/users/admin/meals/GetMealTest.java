package fr.sqli.cantine.controller.users.admin.meals;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GetMealTest extends AbstractContainerConfig implements IMealTest {

    final String paramReq = "?" + "uuidMeal" + "=";

    private IUserDao iStudentDao;
    private IStudentClassDao iStudentClassDao;
    private IMealDao mealDao;
    private MockMvc mockMvc;
    private IFunctionDao functionDao;
    private IUserDao adminDao;
    private String authorizationToken;

    @Autowired
    public GetMealTest(MockMvc mockMvc, IUserDao adminDao, IFunctionDao functionDao, IMealDao mealDao, IUserDao iStudentDao, IStudentClassDao iStudentClassDao) throws Exception {
        this.mockMvc = mockMvc;
        this.adminDao = adminDao;
        this.functionDao = functionDao;
        this.mealDao = mealDao;
        this.iStudentDao = iStudentDao;
        this.iStudentClassDao = iStudentClassDao;
        cleanDb();
        initDb();
    }

    public void initDb() throws Exception {

        // create  Admin and get token
        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        ImageEntity image = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);
        ImageEntity image1 = new ImageEntity();
        image1.setName(SECOND_IMAGE_MEAL_FOR_TEST_NAME);
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        MealEntity mealEntity = new MealEntity("MealTest", "MealTest category", "MealTest description"
                , new BigDecimal("1.5"), 10, 1, mealTypeEnum, image);
        this.mealDao.save(mealEntity);
     /*   this.mealDao.save(meals.get(1));
        this.mealDao.save(meals.get(2));*/
    }


    public void cleanDb() {
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();
        this.mealDao.deleteAll();
    }

    @Test
    @Rollback(true)
    @DisplayName("Get all meals tests  : 2 meals in database")
    public void testGetAllMealsTest() throws Exception {
        ImageEntity image = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);
        MealEntity mealEntity = new MealEntity("platNotAvailable", "MealTest category", "MealTest description"
                , new BigDecimal("1.5"), 10, 1, MealTypeEnum.getMealTypeEnum("ENTREE"), image);
        this.mealDao.save(mealEntity);


        var mealsFound = this.mealDao.findAll();

        // when : get all meals
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_MEALS_URL)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
        );


        // then : 2 meals are returned  (verify status is 200)
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(mealsFound.size())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].label").value(CoreMatchers.is(mealsFound.get(0).getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].category").value(CoreMatchers.is(mealsFound.get(0).getCategory())));

        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].label").value(CoreMatchers.is(mealsFound.get(1).getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].category").value(CoreMatchers.is(mealsFound.get(1).getCategory())));

        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].image").value(Matchers.not(Matchers.isEmptyOrNullString())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].image").value(Matchers.not(Matchers.isEmptyOrNullString())));

    }


    @Test
    void getUnavailableMealsTest() throws Exception {
        ImageEntity image = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);
        MealEntity mealEntity = new MealEntity("platNotAvailable", "MealTest category", "MealTest description"
                , new BigDecimal("1.5"), 10, 0, MealTypeEnum.getMealTypeEnum("ENTREE"), image);
        this.mealDao.save(mealEntity);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_UNAVAILABLE_MEALS_URL).header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].label", CoreMatchers.is("platNotAvailable")));
    }


/**************************************** Get meal by id tests ****************************************/


    @Test
    void getMenuByIdTest() throws Exception {

        var mealUuid = this.mealDao.findAll().get(0).getId();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq + mealUuid).header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.uuid", CoreMatchers.is(mealUuid)));

    }



    @Test
    void getMealByIdWithOutMealNotFound() throws Exception {
        var mealUuid = java.util.UUID.randomUUID().toString(); // id must be not exist in database
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq + mealUuid).header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isNotFound()).andExpect(content().string(super.exceptionMessage(exceptionsMap.get("mealNotFound"))));
    }


    @Test
    void getMealByIdWithInvalidId2() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq + "54fd").header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest()).andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMealUuid"))));
    }


    @Test
    void getMealByIdWithNullId() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq + null).header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest()).andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMealUuid"))));
    }

    @Test
    void getMealByIdWithOutID() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq).header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest()).andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMealUuid"))));
    }

   @Test
    void getMealByIdWithIDWithStudentAuthToken() throws Exception {
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq + java.util.UUID.randomUUID().toString()).header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken));
        result.andExpect(status().isForbidden());

    }

    @Test
    void getMealByIdWithIDWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL + this.paramReq));
        result.andExpect(status().isUnauthorized());

    }

}