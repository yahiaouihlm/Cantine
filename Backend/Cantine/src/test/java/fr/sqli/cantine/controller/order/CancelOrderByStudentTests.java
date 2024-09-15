package fr.sqli.cantine.controller.order;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static fr.sqli.cantine.controller.users.admin.meals.IMealTest.IMAGE_MEAL_FOR_TEST_NAME;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CancelOrderByStudentTests extends AbstractContainerConfig implements IOrderTest {
    private final String requestParam = "?" + "orderUuid" + "=";
    private IPaymentDao paymentDao;
    private IOrderDao iOrderDao;
    private IMealDao mealDao;
    private IMenuDao menuDao;
    private ITaxDao taxDao;
    private MockMvc mockMvc;
    private IUserDao userDao;
    private IFunctionDao functionDao;
    private IStudentClassDao studentClassDao;
    private String authorizationToken;
    private UserEntity studentEntity;
    private MealEntity mealEntity;
    private MealEntity mealEntity2;
    private MenuEntity menuEntity;
    private OrderDtoIn orderDtoIn;
    private OrderEntity orderEntity;

    @Autowired
     public  CancelOrderByStudentTests (IUserDao userDao, IFunctionDao iFunctionDao, IPaymentDao iPaymentDao, IOrderDao iOrderDao, IStudentClassDao studentClassDao, MockMvc mockMvc, IMealDao mealDao, IMenuDao menuDao, ITaxDao taxDao) throws Exception {
             this.userDao = userDao;
            this.functionDao = iFunctionDao;
            this.paymentDao = iPaymentDao;
            this.iOrderDao = iOrderDao;
            this.studentClassDao = studentClassDao;
            this.mockMvc = mockMvc;
            this.mealDao = mealDao;
            this.menuDao = menuDao;
            this.taxDao = taxDao;
            cleanDB();
            initDB();
     }


    void cleanDB() {
        this.iOrderDao.deleteAll();
        this.userDao.deleteAll();
        this.functionDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.taxDao.deleteAll();
        this.paymentDao.deleteAll();

    }

    void initDB() throws Exception {
        this.studentEntity = AbstractLoginRequest.saveAStudent(this.userDao, this.studentClassDao);
        this.authorizationToken=AbstractLoginRequest.getStudentBearerToken(this.mockMvc);

        ImageEntity image = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);

        ImageEntity image2 = new ImageEntity();
        image2.setName(IMAGE_MEAL_FOR_TEST_NAME);

        ImageEntity image3 = new ImageEntity();
        image3.setName(IMAGE_MEAL_FOR_TEST_NAME);

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
    @Disabled
    void cancelOrderByStudent() throws Exception {
        AbstractLoginRequest.saveAdmin(this.userDao, this.functionDao);

        var  order  =  this.iOrderDao.findAll().get(0);
        order.setStatus(0);
        this.iOrderDao.save(order);
        var oldStudentWallet = this.studentEntity.getWallet();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL  + requestParam + order.getId())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string(super.responseMessage(IOrderTest.responseMap.get("OrderCancelledSuccessfully"))));

        var student = this.userDao.findStudentById(this.studentEntity.getId());
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(oldStudentWallet.add(order.getPrice()), student.get().getWallet());
        var payment = this.paymentDao.findAll().get(0);

        Assertions.assertEquals(payment.getOrigin(), TransactionType.REFUNDS);
        Assertions.assertEquals(payment.getAmount(), order.getPrice());
        Assertions.assertEquals(payment.getStudent().getId(), student.get().getId());
        this.paymentDao.deleteAll();

    }


    @Test
    void cancelOrderByStudentAlreadyCancelled() throws Exception {
        var  order  =  this.iOrderDao.findAll().get(0);
        order.setStatus(0);
        order.setCancelled(true);
        this.iOrderDao.save(order);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL  + requestParam + order.getId())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderAlreadyCanceled"))));

    }

    @Test
    void cancelOrderByStudentAlreadyTakenOrder() throws Exception {
        var  order  =  this.iOrderDao.findAll().get(0);
        order.setStatus(2);
        this.iOrderDao.save(order);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL  + requestParam + order.getId())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderAlreadyValidated"))));

    }


    @Test
    void cancelOrderByStudentAlreadyValidatedOrder() throws Exception {
       var  order  =  this.iOrderDao.findAll().get(0);
       order.setStatus(1);
       this.iOrderDao.save(order);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL  + requestParam + order.getId())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderAlreadyValidated"))));

    }

    @Test
    void cancelOrderByStudentWithNotFoundID() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL  + requestParam + java.util.UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("OrderNotFound"))));

    }

    @Test
    void cancelOrderByStudentWithInvalidID() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL  + requestParam + "dejke")
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("InvalidOrderId"))));

    }

    @Test
    void cancelOrderByStudentWithAdminAuthToken() throws Exception {
        AbstractLoginRequest.saveAdmin(this.userDao, this.functionDao);
        var adminAuthorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL + requestParam + java.util.UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, adminAuthorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @Test
    void cancelOrderByStudentWithOutRequestParam() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(IOrderTest.exceptionsMap.get("MissingArgument"))));

    }


    @Test
    void cancelOrderByStudentWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_BY_STUDENT_URL + requestParam + java.util.UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

}
