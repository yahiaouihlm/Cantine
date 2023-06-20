package fr.sqli.Cantine.controller.order;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AddOrderTest {
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

    private Map <String, String>  formData;
    private MealEntity mealEntity;
    private MenuEntity menuEntity;
    private StudentEntity studentEntity;


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
    }

    void  initFormData() {

        this.formData = Map.of(
                "studentId", this.studentEntity.getId().toString(),
                "mealsId", List.of(this.mealEntity.getId()).toString(),
                "menusId", List.of(this.menuEntity.getId()).toString()
        );

    }


    @BeforeEach
    void init() {
        cleanDB();
        initDB();
        initFormData();
    }









    }
