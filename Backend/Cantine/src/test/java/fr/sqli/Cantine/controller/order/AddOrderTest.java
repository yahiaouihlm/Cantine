package fr.sqli.Cantine.controller.order;


import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AddOrderTest   extends AbstractContainerConfig implements   IOrderTest{
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
    private MockMvc mockMvc;

    private OrderDtoIn orderDtoIn;
    private MealEntity mealEntity;
    private MenuEntity menuEntity;
    private StudentEntity studentEntity;
    private ObjectMapper objectMapper = new ObjectMapper();


    void  createMeal  ()   {
        this.mealEntity = new MealEntity();
        this.mealEntity.setLabel("meal");
        this.mealEntity.setDescription("description");
        this.mealEntity.setPrice(BigDecimal.valueOf(5.0));
        this.mealEntity.setQuantity(10);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        this.mealEntity.setImage(imageEntity);
        this.mealEntity.setStatus(1);
        this.mealEntity.setCategory("category");
        this.mealEntity.setQuantity(10);
        this.mealEntity  =    this.mealDao.save(this.mealEntity);
    }

    void  createMenu () {
        this.menuEntity = new MenuEntity();
        this.menuEntity.setLabel("menu");
        this.menuEntity.setDescription("description");
        this.menuEntity.setPrice(BigDecimal.valueOf(5.0));
        this.menuEntity.setQuantity(10);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        this.menuEntity.setCreatedDate(LocalDate.now());
        this.menuEntity.setImage(imageEntity);
        this.menuEntity.setStatus(1);
        this.menuEntity.setMeals(List.of(this.mealEntity));
        this.menuEntity.setQuantity(10);
        this.menuEntity  =    this.menuDao.save(this.menuEntity);
    }

    void createStudent() {
        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("class");
        this.studentClassDao.save(studentClassEntity);

        this.studentEntity = new StudentEntity();
        this.studentEntity.setStudentClass(studentClassEntity);
        this.studentEntity.setFirstname("firstname");
        this.studentEntity.setLastname("lastname");
        this.studentEntity.setEmail("yahiaouihlm@gmail.com");
        this.studentEntity.setWallet(BigDecimal.valueOf(500));
        this.studentEntity.setStatus(1);
        this.studentEntity.setTown("town");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        this.studentEntity.setImage(imageEntity);
        this.studentEntity.setBirthdate(LocalDate.now());
        this.studentEntity.setRegistrationDate(LocalDate.now());
        this.studentEntity.setPassword("password");
        this.studentEntity = this.studentDao.save(this.studentEntity);
    }


    void  initDB() {
       createMeal();
       createMenu();
       createStudent();


    }

    void  cleanDB() {
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




    }
