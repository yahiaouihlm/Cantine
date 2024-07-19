package fr.sqli.cantine.controller.order;


import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.entity.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static fr.sqli.cantine.controller.users.admin.meals.IMealTest.IMAGE_MEAL_FOR_TEST_NAME;


@SpringBootTest
@AutoConfigureMockMvc
public class AddOrderTest extends AbstractLoginRequest implements IOrderTest {
    @Autowired
    private Environment env;
    private IOrderDao iOrderDao;
    private IMealDao mealDao;
    private IMenuDao menuDao;
    private ITaxDao taxDao;
    private MockMvc mockMvc;
    private IUserDao studentDao;
    private IStudentClassDao studentClassDao;
    private String authorizationToken;
    private UserEntity studentEntity;
    private MealEntity mealEntity;
    private MealEntity mealEntity2;
    private MenuEntity menuEntity;
    private OrderDtoIn orderDtoIn;

    @Autowired
    public AddOrderTest( IOrderDao iOrderDao , IUserDao studentDao, IStudentClassDao studentClassDao, MockMvc mockMvc, IMealDao mealDao, IMenuDao menuDao, ITaxDao taxDao) throws Exception {
        this.studentDao = studentDao;
        this.studentClassDao = studentClassDao;
        this.mockMvc = mockMvc;
        this.mealDao = mealDao;
        this.menuDao = menuDao;
        this.taxDao = taxDao;
        this.iOrderDao = iOrderDao;
        cleanDB();
        initDB();
        initRequestData();

    }

    void cleanDB() {
        this.iOrderDao.deleteAll();
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.taxDao.deleteAll();
    }

    void initDB() throws Exception {
        this.studentEntity = AbstractLoginRequest.saveAStudent(this.studentDao, this.studentClassDao);
        this.authorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);


        ImageEntity image = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);

        ImageEntity image2 = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);

        ImageEntity image3 = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);

        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        var mealEntity = new MealEntity("MealTest", "MealTest category", "MealTest description", new BigDecimal("1.5"), 10, 1, mealTypeEnum, image);
        var mealEntity2 = new MealEntity("MealTest2", "MealTest category 1", "MealTest description second", new BigDecimal("15"), 10, 1, mealTypeEnum, image2);

        this.mealEntity = this.mealDao.save(mealEntity);
        this.mealEntity2 = this.mealDao.save(mealEntity2);

        var menuEntity = new MenuEntity("MenuTest", "MenuTest description", new BigDecimal("5"), 1, 10, image3, Set.of(mealEntity, mealEntity2));
        this.menuEntity = this.menuDao.save(menuEntity);

        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(2));
        this.taxDao.save(taxEntity);


    }

    void initRequestData() throws JsonProcessingException {
        this.orderDtoIn = new OrderDtoIn();
        this.orderDtoIn.setStudentUuid(this.studentEntity.getId());
        this.orderDtoIn.setMealsId(List.of(this.mealEntity.getId(), this.mealEntity2.getId()));
        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId()));

    }

    @Test
    void addOrderTest() throws Exception {

        var studentWallet = BigDecimal.valueOf(100);
        this.studentEntity.setWallet(studentWallet);
        this.studentDao.save(this.studentEntity);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));


        result.andExpect(MockMvcResultMatchers.status().isOk());

        result.andExpect(MockMvcResultMatchers.content().string(super.responseMessage(IOrderTest.responseMap.get("OrderAddedSuccessfully"))));

        var savedOrder = this.iOrderDao.findAll().get(0);

        Assertions.assertEquals(savedOrder.getStudent().getId(), this.orderDtoIn.getStudentUuid());

        var totalPrice = this.mealEntity.getPrice().add(this.mealEntity2.getPrice()).add(this.menuEntity.getPrice()).add(this.taxDao.findAll().get(0).getTax());
        Assertions.assertEquals(totalPrice, savedOrder.getPrice());

        var newStudentWallet = this.studentEntity.getWallet().subtract(totalPrice);

        Assertions.assertEquals(newStudentWallet, this.studentDao.findStudentById(this.studentEntity.getId()).get().getWallet());
        this.iOrderDao.deleteAll();
    }

    @Test
    void addOrderWithEnoughStudentWallet() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isPaymentRequired());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InsufficientBalance"))));
    }


    /**********************************  TESTS Order With Tax  ********************************/
    @Test
    void addOrderWithOutTaxInDB() throws Exception {
        this.taxDao.deleteAll();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isInternalServerError());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("TaxNotFound"))));

        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(2));
        this.taxDao.save(taxEntity);
    }

    @Test
    void addOrderWithTwoTaxInDB() throws Exception {
        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(4));
        this.taxDao.save(taxEntity);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isInternalServerError());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("TaxNotFound"))));
    }


    /**********************************  TESTS Order With Meal  Or  Menu  Not  Found ********************************/
    @Test
    void addOrderWitMenuNotFoundTest() throws Exception {

        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId(), java.util.UUID.randomUUID().toString()));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));


        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("MenuNotFound"))));
    }


    @Test
    void addOrderWitMealNotFoundTest() throws Exception {

        this.orderDtoIn.setMealsId(List.of(this.mealEntity.getId(), java.util.UUID.randomUUID().toString()));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));


        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("MealNotFound"))));
    }


    /**********************************  TESTS Order Limits  ********************************/
    @Test
    void addOrderWitExceedMenuAndMealsOrderLimitTest() throws Exception {
        this.orderDtoIn.setMealsId(IntStream.range(0, 11)
                .mapToObj(i -> i % 2 == 0 ? this.mealEntity.getId() : this.mealEntity2.getId())
                .collect(Collectors.toList()));

        this.orderDtoIn.setMenusId(IntStream.range(0, 10)
                .mapToObj(i -> this.mealEntity.getId())
                .collect(Collectors.toList()));


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderLimit"))));
    }

    @Test
    void addOrderWitExceedMenuOrderLimitTest() throws Exception {
        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(IntStream.range(0, 21)
                .mapToObj(i -> this.mealEntity.getId())
                .collect(Collectors.toList()));


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderLimit"))));
    }


    @Test
    void addOrderWitExceedMealOrderLimitTest() throws Exception {
        this.orderDtoIn.setMenusId(List.of());
        this.orderDtoIn.setMealsId(IntStream.range(0, 21)
                .mapToObj(i -> i % 2 == 0 ? this.mealEntity.getId() : this.mealEntity2.getId())
                .collect(Collectors.toList()));


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderLimit"))));
    }


    /**********************************  TESTS  MEALS  AND  MENUS   IDs ********************************/

    @Test
    void addOrderWithRemovedMenu() throws Exception {
        this.menuEntity.setStatus(2);
        this.menuDao.save(this.menuEntity);
        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId()));
        String exceptionMessage = "MENU  : " + this.menuEntity.getLabel() + " IS UNAVAILABLE OR REMOVED";

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionMessage)));

    }


    @Test
    void addOrderWithUnavailableMenu() throws Exception {
        this.menuEntity.setStatus(0);
        this.menuDao.save(this.menuEntity);
        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId()));

        String exceptionMessage = "MENU  : " + this.menuEntity.getLabel() + " IS UNAVAILABLE OR REMOVED";
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionMessage)));

    }


    @Test
    void addOrderWithRemovedMeal() throws Exception {
        this.mealEntity.setStatus(2);
        this.mealDao.save(this.mealEntity);
        this.orderDtoIn.setMealsId(List.of(this.mealEntity.getId(), this.mealEntity2.getId()));

        String exceptionMessage = "MEAL  : " + this.mealEntity.getLabel() + " IS UNAVAILABLE OR REMOVED";
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionMessage)));

    }


    @Test
    void addOrderWithUnavailableMeal() throws Exception {
        this.mealEntity.setStatus(0);
        this.mealDao.save(this.mealEntity);
        this.orderDtoIn.setMealsId(List.of(this.mealEntity.getId(), this.mealEntity2.getId()));

        String exceptionMessage = "MEAL  : " + this.mealEntity.getLabel() + " IS UNAVAILABLE OR REMOVED";
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionMessage)));

    }


    @Test
    void addOrderWithInvalidMenusId() throws Exception {

        this.orderDtoIn.setMenusId(List.of("invalidID", "zkononzf"));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidMenuId"))));

    }

    @Test
    void addOrderWithInvalidMealsId() throws Exception {

        this.orderDtoIn.setMealsId(List.of("invalidID", "zkononzf"));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidMealId"))));

    }

    @Test
    void addOrderWithEmptyMealAndMenu() throws Exception {

        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of());

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("EmptyMealsAndMenus"))));

    }

    @Test
    void addOrderWithNullMealAndMenu() throws Exception {

        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("MealsOrMenusAreRequired"))));

    }

    /**********************************  TESTS  STUDENT  ID ********************************/


    @Test
    void addOrderWithNotFoundStudentId() throws Exception {
        this.orderDtoIn.setStudentUuid(java.util.UUID.randomUUID().toString());   //    be sure  that  we  get  a  student  Does  not  exist


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("studentNotFound"))));

    }


    @Test
    void addOrderWithInvalidStudentId() throws Exception {
        this.orderDtoIn.setStudentUuid("sdfzrzrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("StudentIsRequired"))));

    }


    @Test
    void addOrderWithOutStudentId() throws Exception {
        this.orderDtoIn.setStudentUuid(null);

        // make    directly  the  request  without  studentId
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(new ObjectMapper().writeValueAsString(orderDtoIn)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("StudentIsRequired"))));

    }

    @Test
    void addOrderWithWrongJsonRequest() throws Exception {
        // make    directly  the  request  without  studentId
        String jsonRequest = """
                     {
                     zsfzef
                        "mealsId": [90, 85, 95, 88
                }""";
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .content(jsonRequest));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidJsonFormat"))));

    }


    @Test
    void addOrderWithNullRequest() throws Exception {

        var result = this.mockMvc.perform(
                MockMvcRequestBuilders.post(ADD_ORDER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidJsonFormat"))));

    }


    @Test
    void addOrderWithOutStudentAuthToken() throws Exception {
        // make    directly  the  request  without  studentId

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }


}

