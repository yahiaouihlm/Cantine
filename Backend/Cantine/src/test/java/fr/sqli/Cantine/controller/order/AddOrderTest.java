package fr.sqli.Cantine.controller.order;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.List;

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

    private MealEntity mealEntity;
    private MenuEntity menuEntity;
    private StudentEntity studentEntity;

    void  createMeal  ()   {
        this.mealEntity = new MealEntity();
        this.mealEntity.setLabel("meal");
        this.mealEntity.setDescription("description");
        this.mealEntity.setPrice(BigDecimal.valueOf(5.0));
        this.mealEntity.setQuantity(10);
        this.mealEntity.setImage(new ImageEntity());
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
        this.menuEntity.setImage(new ImageEntity());
        this.menuEntity.setStatus(1);
        this.menuEntity.setMeals(List.of(this.mealEntity));
        this.menuEntity.setQuantity(10);
        this.menuEntity  =    this.menuDao.save(this.menuEntity);
    }
    void  initDB() {





    }

    void  cleanDB() {
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.studentDao.deleteAll();
    }

}
