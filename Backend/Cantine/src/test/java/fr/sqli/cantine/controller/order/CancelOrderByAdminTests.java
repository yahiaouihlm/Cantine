package fr.sqli.cantine.controller.order;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static fr.sqli.cantine.controller.users.admin.meals.IMealTest.IMAGE_MEAL_FOR_TEST_NAME;

@SpringBootTest
@AutoConfigureMockMvc
public class CancelOrderByAdminTests extends AbstractContainerConfig implements IOrderTest {

    private final String requestParam = "?" + "orderUuid" + "=";
    private IPaymentDao paymentDao;
    private IOrderDao iOrderDao;
    private IMealDao mealDao;
    private IMenuDao menuDao;
    private ITaxDao taxDao;
    private MockMvc mockMvc;
    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private IStudentDao studentDao;
    private IStudentClassDao studentClassDao;
    private String authorizationToken;
    private StudentEntity studentEntity;
    private MealEntity mealEntity;
    private MealEntity mealEntity2;
    private MenuEntity menuEntity;
    private OrderDtoIn orderDtoIn;
    private OrderEntity orderEntity;


    @Autowired
    public CancelOrderByAdminTests(IAdminDao iAdminDao, IFunctionDao iFunctionDao, IPaymentDao iPaymentDao, IOrderDao iOrderDao, IStudentDao studentDao, IStudentClassDao studentClassDao, MockMvc mockMvc, IMealDao mealDao, IMenuDao menuDao, ITaxDao taxDao) throws Exception {
        this.paymentDao = iPaymentDao;
        this.studentDao = studentDao;
        this.studentClassDao = studentClassDao;
        this.mockMvc = mockMvc;
        this.mealDao = mealDao;
        this.menuDao = menuDao;
        this.taxDao = taxDao;
        this.iOrderDao = iOrderDao;
        this.adminDao = iAdminDao;
        this.functionDao = iFunctionDao;
        cleanDB();
        initDB();

    }


    void cleanDB() {
        this.iOrderDao.deleteAll();
        this.studentDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.taxDao.deleteAll();
        this.paymentDao.deleteAll();

    }

    void initDB() throws Exception {
        this.studentEntity = AbstractLoginRequest.saveAStudent(this.studentDao, this.studentClassDao);

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);


        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);

        ImageEntity image2 = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);

        ImageEntity image3 = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);

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

        this.orderEntity = new OrderEntity();
        orderEntity.setStudent(this.studentEntity);
        orderEntity.setMeals(List.of(this.mealEntity, this.mealEntity2));
        orderEntity.setMenus(List.of(this.menuEntity));
        orderEntity.setCreationDate(LocalDate.now());
        orderEntity.setPrice(this.mealEntity.getPrice().add(this.mealEntity2.getPrice()).add(this.menuEntity.getPrice()).add(taxEntity.getTax()));
        orderEntity.setStatus(0);
        orderEntity.setQRCode("QRCode");
        orderEntity.setCreationTime(new Time(System.currentTimeMillis()));
        this.iOrderDao.save(orderEntity);

    }


    @Test
    void cancelOrderByAdmin() throws Exception {
        var oldStudentWallet = this.studentEntity.getWallet();

        var order = this.iOrderDao.findAll().get(0);
        order.setStatus(0);
        var orderPrice = order.getPrice();
        this.iOrderDao.save(order);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + order.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string(super.responseMessage(IOrderTest.responseMap.get("OrderCancelledSuccessfully"))));

        var student = this.studentDao.findByUuid(this.studentEntity.getUuid());
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(oldStudentWallet.add(orderPrice), student.get().getWallet());
        var payment = this.paymentDao.findAll().get(0);

        Assertions.assertEquals(payment.getOrigin(), TransactionType.REFUNDS);
        Assertions.assertEquals(payment.getAmount(), orderPrice);
        Assertions.assertEquals(payment.getStudent().getUuid(), student.get().getUuid());
        this.paymentDao.deleteAll();

    }


    @Test
    void cancelOrderByAdminWithCancelledOrder() throws Exception {
        var order = this.iOrderDao.findAll().get(0);
        order.setStatus(0);
        order.setCancelled(true);
        this.iOrderDao.save(order);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + order.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderAlreadyCanceled"))));
    }


    @Test
    void cancelOrderByAdminWithTakenOrder() throws Exception {
        var order = this.iOrderDao.findAll().get(0);
        order.setStatus(2);
        this.iOrderDao.save(order);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + order.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderAlreadyValidated"))));
    }

    @Test
    void cancelOrderByAdminWithValidatedOrder() throws Exception {
        var order = this.iOrderDao.findAll().get(0);
        order.setStatus(1);
        this.iOrderDao.save(order);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + order.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderAlreadyValidated"))));
    }

    @Test
    void cancelOrderByAdminWithOrderNotFound() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + java.util.UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderNotFound"))));
    }

    /*************************************************** TESTS ORDER  ID ***************************************************/

    @Test
    void cancelOrderByAdminWithInvalidOrderId() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + "invalUUID")
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidOrderId"))));
    }


    @Test
    void cancelOrderByAdminWithNullRequestParam() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + null)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidOrderId"))));

    }


    @Test
    void cancelOrderByAdminWithStudentAuth() throws Exception {
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + java.util.UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void cancelOrderByAdminWithOutRequestParam() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("MissingArgument"))));

    }


    @Test
    void cancelOrderByAdminWithOutAuthToken() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_ADMIN_URL + requestParam + java.util.UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }


}


