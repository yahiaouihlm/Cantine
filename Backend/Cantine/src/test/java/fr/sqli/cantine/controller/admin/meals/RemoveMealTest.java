package fr.sqli.cantine.controller.admin.meals;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.student.IStudentTest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMealTest extends AbstractContainerConfig implements IMealTest {

    final String paramReq = "?" + "idMeal" + "=";
    //"THE ID CAN NOT BE NULL OR LESS THAN 0"
    final String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    @Autowired
    private IMealDao mealDao;

    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IStudentClassDao studentClassDao;
    @Autowired
    private IStudentDao studentDao;

    @Autowired
    private IOrderDao orderDao;
    private List<MealEntity> meals;





    public void initDB() {
        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);
        ImageEntity image1 = new ImageEntity();
        image1.setImagename(SECOND_IMAGE_MEAL_FOR_TEST_NAME);

        this.meals =
                List.of(
                        new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1, 1, image),
                        new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1, 1, image1)
                );
        this.mealDao.saveAll(meals);
    }


    void cleanUp() {
        this.orderDao.deleteAll();
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.menuDao.deleteAll();
        this.mealDao.deleteAll(); // clean  data  after  each  test
    }
    @BeforeAll
    static void  copyImageTestFromTestDirectoryToImageMenuDirectory() throws IOException {
        String source = IMAGE_MEAL_TEST_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        String destination = IMAGE_MEAL_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        File sourceFile = new File(source);
        File destFile = new File(destination);
        if  (!destFile.exists())
            Files.copy(sourceFile.toPath(), destFile.toPath());
    }




    @BeforeEach
    void init (){
       this.cleanUp();
        this.initDB();
    }
    BufferedImage saveTestFile() throws IOException {
        File image = new File(IMAGE_MEAL_FOR_TEST_PATH);
        return ImageIO.read(image);
    }

    @Test
    void removeMealTest() throws Exception {
        var mealToRemove = this.mealDao.findAll().get(0);



        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq + mealToRemove.getId()));


        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(MEAL_DELETED_SUCCESSFULLY)));

        Assertions.assertEquals(1, this.mealDao.findAll().size());
        var  image =  mealToRemove.getImage().getImagename();
        Assertions.assertFalse(new File(IMAGE_MEAL_DIRECTORY_PATH + image).exists());



    }

    /*  TODO whe We Make Menu */
    @Test
    void removeMealInAssociationWithMenu() throws Exception {
        var idMealToRemove = this.mealDao.findAll().get(0);
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setLabel("menu1");
        menuEntity.setPrice(new BigDecimal("2.3"));
        menuEntity.setDescription("menu1 description test ");
        menuEntity.setQuantity(1);
        menuEntity.setStatus(1);
        menuEntity.setCreatedDate(LocalDate.now());
        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);
        menuEntity.setImage(image);
        menuEntity.setMeals(List.of(idMealToRemove));
        var   removeMeal   =   this.menuDao.save(menuEntity);

        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq + idMealToRemove.getId()));

   var resultReq = " Le  Plat  \" " + removeMeal.getLabel() + " \"  Ne  Pas Etre  Supprimé  Car  Il  Est  Present  Dans  d'autres  Menu(s)" ;
        result.andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(resultReq)));
    }

    @Test
    void removeMealInAssociationWithOrder() throws Exception {

        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("class1");
        studentClassEntity =  this.studentClassDao.save(studentClassEntity);
       var student   =    IStudentTest.createStudentEntity("yahiaoui@gmail.com" , studentClassEntity);
       student = this.studentDao.save(student);
        var  idMealToRemove = this.mealDao.findAll().get(0);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStudent(student);
        orderEntity.setQRCode("qrcode");
        orderEntity.setPrice(new BigDecimal("2.3"));
        orderEntity.setCreationDate(LocalDate.now());
        orderEntity.setMeals(List.of(idMealToRemove));
        orderEntity.setStatus(1);
       orderEntity.setCreationTime(new Time(System.currentTimeMillis()));
       this.orderDao.save(orderEntity);

        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq + idMealToRemove.getId()));

        result.andExpect(status().isConflict());

    }



    @Test
    void removeMealTestWithMealNotFound() throws Exception {

        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq + (Integer.MAX_VALUE - 10)));

        result.andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("mealNotFound"))));

    }

    @Test
    void removeMealTestWithNegativeID() throws Exception {
        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL +this.paramReq+"-5"));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidParameter"))));

    }


    @Test
    void removeMealTestWithInValidID2() throws Exception {
        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL +this.paramReq+ "1000000000000000000000000000000000000000000"));

        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }

    @Test
    void removeMealTestWithInvalidID() throws Exception {
        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq+"ozzedoz"));

        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }


    @Test
    void removeMealTestWithNullID() throws Exception {
        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq + null    ));

        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }

    @Test
    void removeMealTestWithOutId() throws Exception {
        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + this.paramReq  ));


        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("missingParam"))));

    }


}
