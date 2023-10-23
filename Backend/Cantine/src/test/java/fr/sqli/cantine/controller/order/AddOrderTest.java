package fr.sqli.cantine.controller.order;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.entity.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AddOrderTest   extends AbstractLoginRequest implements   IOrderTest{
    @Autowired
    private Environment env;
    @Autowired
    private IMealDao mealDao;
    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private IStudentClassDao studentClassDao;
    @Autowired
    private ITaxDao  taxDao;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IOrderDao orderDao;


    private  String  adminAuthorizationToken;

    private OrderDtoIn orderDtoIn;
    private MealEntity mealEntity;
    private MenuEntity menuEntity;
    private StudentEntity studentEntity;
    private  TaxEntity  taxEntity;
    private ObjectMapper objectMapper = new ObjectMapper();



    void  createMeal  ()   {
        this.mealEntity = IOrderTest.createMeal();
        this.mealEntity  =    this.mealDao.save(this.mealEntity);
    }

    void  createMenu () {
        this.menuEntity = IOrderTest.createMenu(this.mealEntity);
        this.menuEntity  =    this.menuDao.save(this.menuEntity);
    }

    void createStudent() {
        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("class");
        this.studentClassDao.save(studentClassEntity);

        this.studentEntity = IOrderTest.createStudent("yahiaoui@gmail.com",  studentClassEntity);

        this.studentEntity = this.studentDao.save(this.studentEntity);
    }


    void  initDB() {

        TaxEntity  taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(2));
        this.taxEntity = this.taxDao.save(taxEntity);


       createMeal();
       createMenu();
       createStudent();


    }

    void  cleanDB() {
        this.orderDao.deleteAll();
        this.taxDao.deleteAll();
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();

    }

    void  initFormData() {
        this.orderDtoIn =  new OrderDtoIn();
        this.orderDtoIn.setStudentId(this.studentEntity.getId());
        this.orderDtoIn.setMealsId(List.of(this.mealEntity.getId()));
        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId()));

    }


    @BeforeEach
    void init() {
        cleanDB();
        initDB();
        initFormData();
    }



    @Test
    void  addOrderTest () throws Exception {

        var someprice =  this.mealEntity.getPrice().add(this.menuEntity.getPrice()).add(this.taxEntity.getTax());
        var  studentWallet = this.studentEntity.getWallet();
        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string(ORDER_ADDED_SUCCESSFULLY));

        var savedStudent =  this.studentDao.findById(this.studentEntity.getId()).get();
       var  savedOrder =  this.orderDao.findByStudentId(savedStudent.getId()).get(0);

        Assertions.assertTrue(someprice.compareTo(savedOrder.getPrice()) == 0 );

        Assertions.assertTrue(studentWallet.subtract(someprice).compareTo(savedStudent.getWallet()) == 0 );
        var  qrcodeImagePath  =  this.env.getProperty("sqli.canine.order.qrcode.path")+savedOrder.getQRCode() ;
        File file = new File(qrcodeImagePath );


        System.out.println(env.getProperty("sqli.canine.order.qrcode.path")+savedOrder.getQRCode());
        Assertions.assertTrue(file.exists());
       Assertions.assertTrue(file.delete());


    }




    @Test
    void  addOrderWithEnoughStudentWallet () throws Exception {
        this.studentEntity.setWallet(BigDecimal.valueOf(1));
        this.studentDao.save(this.studentEntity);

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));
        result.andExpect(MockMvcResultMatchers.status().isPaymentRequired());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InsufficientBalance"))));
    }





    /**********************************  TESTS Order With Tax  ********************************/

    @Test
    void  addOrderWithOutTaxInDB () throws Exception {
        this.taxDao.deleteAll();

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));
        result.andExpect(MockMvcResultMatchers.status().isInternalServerError());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("TaxNotFound"))));
    }

    @Test
     void  addOrderWithTwoTaxInDB () throws Exception {
         TaxEntity  taxEntity = new TaxEntity();
            taxEntity.setTax(BigDecimal.valueOf(4));
          this.taxDao.save(taxEntity);

         var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

         var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                 ).contentType(MediaType.APPLICATION_JSON)
                 .content(requestdata));
         result.andExpect(MockMvcResultMatchers.status().isInternalServerError());
            result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("TaxNotFound"))));
     }



    /**********************************  TESTS Order With Meal  Or  Menu   Unavailable   ********************************/


    @Test

    void addOrderWitMenuUnavailableAndExistingOneTest() throws Exception {
        this.menuEntity.setStatus(0);
        var  savedMenu=  this.menuDao.save(this.menuEntity);
        var  menuUnavailableId =  savedMenu.getId();
        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of(menuUnavailableId,   2 )  );

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("MenuForUnavailable") + savedMenu.getLabel() + discontinuedexceptionsMap.get("Unavailable"))));
    }


    @Test

    void addOrderWitMealUnavailableAndOtherOneTest() throws Exception {
        this.mealEntity.setStatus(0);
        var  savedMeal =  this.mealDao.save(this.mealEntity);
        var  mealUnavailableId =  savedMeal.getId();
        this.orderDtoIn.setMealsId(List.of(mealUnavailableId , 2 ));
        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId()) );

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("MealForUnavailable") + savedMeal.getLabel() + discontinuedexceptionsMap.get("Unavailable"))) ) ;
    }








    @Test
    void  addOrderWitMenuUnavailableTest() throws Exception {
        this.menuEntity.setStatus(0);
        var  savedMenu=  this.menuDao.save(this.menuEntity);
        var  menuUnavailableId =  savedMenu.getId();
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(List.of(menuUnavailableId) );

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("MenuForUnavailable") + savedMenu.getLabel() + discontinuedexceptionsMap.get("Unavailable"))));
    }




    @Test
    void  addOrderWitMealUnavailableTest() throws Exception {
         this.mealEntity.setStatus(0);
        var  savedMeal =  this.mealDao.save(this.mealEntity);
        var  mealUnavailableId =  savedMeal.getId();
        this.orderDtoIn.setMealsId(List.of(mealUnavailableId));
        this.orderDtoIn.setMenusId(null );

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("MealForUnavailable") + savedMeal.getLabel() + discontinuedexceptionsMap.get("Unavailable"))));
    }






    /**********************************  TESTS Order With Meal  Or  Menu  Not  Found ********************************/
   @Test

   void  addOrderWitMenuNotFoundAndExistingOneTest() throws Exception {
       var  menuNotFound =  this.menuDao.findById(this.menuEntity.getId()).get().getId() + 4;
       this.orderDtoIn.setMenusId(List.of(menuNotFound , this.menuEntity.getId()));
       this.orderDtoIn.setMealsId(List.of(this.mealEntity.getId()) );

       var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

       var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
               ).contentType(MediaType.APPLICATION_JSON)
               .content(requestdata));

       result.andExpect(MockMvcResultMatchers.status().isNotFound());
       result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("MenuFor") + menuNotFound + discontinuedexceptionsMap.get("NotFound"))));
   }


    @Test

    void  addOrderWitMealNotFoundAndExistingOneTest() throws Exception {
        var  mealNotFoundId =  this.mealDao.findById(this.mealEntity.getId()).get().getId() + 4;
        this.orderDtoIn.setMealsId(List.of(mealNotFoundId ,  this.mealEntity.getId()));
        this.orderDtoIn.setMenusId(List.of(this.menuEntity.getId() ) );

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("NealFor") + mealNotFoundId + discontinuedexceptionsMap.get("NotFound"))) ) ;
    }

   @Test

   void  addOrderWitMenuNotFoundTest() throws Exception {
       var  menuNotFound =  this.menuDao.findById(this.menuEntity.getId()).get().getId() + 4;
       this.orderDtoIn.setMenusId(List.of(menuNotFound));
       this.orderDtoIn.setMealsId(null );

       var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

       var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
               ).contentType(MediaType.APPLICATION_JSON)
               .content(requestdata));

       result.andExpect(MockMvcResultMatchers.status().isNotFound());
       result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("MenuFor") + menuNotFound + discontinuedexceptionsMap.get("NotFound"))));
   }


    @Test

   void  addOrderWitMealNotFoundTest() throws Exception {
       var  mealNotFoundId =  this.mealDao.findById(this.mealEntity.getId()).get().getId() + 4;
       this.orderDtoIn.setMealsId(List.of(mealNotFoundId));
       this.orderDtoIn.setMenusId(null );

       var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

       var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
               ).contentType(MediaType.APPLICATION_JSON)
               .content(requestdata));

       result.andExpect(MockMvcResultMatchers.status().isNotFound());
       result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(discontinuedexceptionsMap.get("NealFor") + mealNotFoundId + discontinuedexceptionsMap.get("NotFound"))));
   }




    /**********************************  TESTS Order Limits  ********************************/

    @Test

    void  addOrderWitExceedMenuAndMealOrderLimitTest() throws Exception {
        this.orderDtoIn.setMealsId(List.of(1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 ));
        this.orderDtoIn.setMenusId(List.of(1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10  ));

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("OrderLimit"))));
    }

    @Test

    void  addOrderWitExceedMenuOrderLimitTest() throws Exception {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(List.of(1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 ,11 ,12 ,13 ,14 ,15 ,16 ,17 ,18 ,19 ,20 , 21 ));

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("OrderLimit"))));
    }



    @Test

    void  addOrderWitExceedMealOrderLimitTest() throws Exception {
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 ,11 ,12 ,13 ,14 ,15 ,16 ,17 ,18 ,19 ,20 , 21 ));

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("OrderLimit"))));
    }





    /**********************************  TESTS  MEALS  AND  MENUS   IDs ********************************/

    @Test
    void addOrderWithNegativeMenuId () throws Exception {
        this.orderDtoIn.setMenusId(List.of(1 ,  -3  ));   //    be sure  that  we  get  a  student  Does  not  exist
        this.orderDtoIn.setMealsId(null);

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuId"))));

    }

    @Test
    void addOrderWithNegativeMealId () throws Exception {
        this.orderDtoIn.setMealsId(List.of(1 ,  -3  ));   //    be sure  that  we  get  a  student  Does  not  exist
        this.orderDtoIn.setMenusId(null);

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InvalidMealId"))));

    }

    @Test
    void addOrderWithOutMenuAndMealId () throws Exception {
        this.orderDtoIn.setMealsId(null);   //    be sure  that  we  get  a  student  Does  not  exist
        this.orderDtoIn.setMenusId(null);

        var   requestdata = this.objectMapper.writeValueAsString(this.orderDtoIn);

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("MealsOrMenusAreRequired"))));

    }






    /**********************************  TESTS  STUDENT  ID ********************************/


    @Test
    void addOrderWithNotFoundStudentId () throws Exception {
         this.orderDtoIn.setStudentId(this.studentEntity.getId() + 5 );   //    be sure  that  we  get  a  student  Does  not  exist

        var   requestdata = this.objectMapper.writeValueAsString(orderDtoIn);
        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("studentNotFound"))));

    }



    @Test
    void addOrderWithNegativeStudentId () throws Exception {
        this.orderDtoIn.setStudentId(-3);   //    be sure  that  we  get  a  student  Does  not  exist

        var   requestdata = this.objectMapper.writeValueAsString(orderDtoIn);
        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("StudentIsRequired"))));

    }



    @Test
    void addOrderWithInvalidStudentId () throws Exception {
        String  jsonRequest = """
             {
                "studentId": zenzklenfkz,
                "mealsId": [90, 85, 95, 88, 92],
                "menusId": [1, 2, 3, 4, 5]
        }""" ;

        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InvalidJsonFormat"))));

    }





    @Test
    void addOrderWithNullStudentId () throws Exception {
        this.orderDtoIn.setStudentId(null);
        var   requestdata = this.objectMapper.writeValueAsString(orderDtoIn);
        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requestdata));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("StudentIsRequired"))));

    }



    @Test
       void addOrderWithOutStudentId () throws Exception {
        // make    directly  the  request  without  studentId
     String  jsonRequest = """
             {
                "mealsId": [90, 85, 95, 88, 92],
                "menusId": [1, 2, 3, 4, 5]
        }""" ;
          var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                  ).contentType(MediaType.APPLICATION_JSON)
                  .content(jsonRequest));

            result.andExpect(MockMvcResultMatchers.status().isBadRequest());
            result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("StudentIsRequired"))));

       }



    @Test
    void addOrderWithWrongJsonRequest() throws Exception {
        // make    directly  the  request  without  studentId
        String  jsonRequest = """
             {
             zsfzef
                "mealsId": [90, 85, 95, 88
        }""" ;
        var result =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InvalidJsonFormat"))));

    }

    @Test
    void addOrderWithNullRequest() throws Exception {
        // make    directly  the  request  without  studentId

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_ORDER_URL
                ).contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InvalidJsonFormat"))));

    }


    }
