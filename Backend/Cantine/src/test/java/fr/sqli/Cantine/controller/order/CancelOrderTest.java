package fr.sqli.Cantine.controller.order;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class CancelOrderTest  extends AbstractContainerConfig implements   IOrderTest {

     final String  ReqParam = "?&orderId=1";


    @Autowired
    private IOrderDao orderDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private IMealDao mealDao;
    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private IStudentClassDao studentClassDao;
    private MealEntity mealEntity;
    private MenuEntity menuEntity;
    private StudentEntity studentEntity;
    private OrderEntity orderEntity;

    void cleanDataBase ()  {
        this.orderDao.deleteAll();
        this.studentDao.deleteAll();
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.studentClassDao.deleteAll();
    }

    void  createInfoForOrder  ()   {
        this.mealEntity = IOrderTest.createMeal();
        this.mealEntity = this.mealDao.save(this.mealEntity);

        this.menuEntity = IOrderTest.createMenu(this.mealEntity);
        this.menuEntity = this.menuDao.save(this.menuEntity);

        StudentClassEntity studentClassEntity =new  StudentClassEntity();
        studentClassEntity.setName("JAVA SQLI");

        studentClassEntity =  this.studentClassDao.save(studentClassEntity);

        this.studentEntity =  IOrderTest.createStudent( "yahiaoui@halim.com" ,  studentClassEntity);
        this.studentDao.save(this.studentEntity);

    }


    void createOrder  ()   {
        var  tax =  BigDecimal.valueOf(2);
        this.orderEntity = new OrderEntity();
        this.orderEntity.setStudent(this.studentEntity);
        this.orderEntity.setMeals(List.of(this.mealEntity));
        this.orderEntity.setMenus(List.of(this.menuEntity));
        this.orderEntity.setPrice(this.mealEntity.getPrice().add(this.menuEntity.getPrice()).add(tax));
        this.orderEntity.setCreationDate(LocalDate.now());
        this.orderEntity.setQRCode("QRCode"); // just because  we  dont  need to create   Image QRCODE for  this  test
        this.orderEntity.setCreationTime(new Time(System.currentTimeMillis()));
        this.orderEntity.setStatus(0);
        this.orderDao.save(this.orderEntity);
    }


    @BeforeEach
    void setUp() {
        this.cleanDataBase();
        this.createInfoForOrder();
        this.createOrder();
    }







    @Test
    void cancelOrderWithNullId() throws Exception {
    }








}
