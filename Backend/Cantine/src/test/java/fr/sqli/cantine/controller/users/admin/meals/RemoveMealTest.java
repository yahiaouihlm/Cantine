package fr.sqli.cantine.controller.users.admin.meals;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.controller.users.student.IStudentTest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMealTest extends AbstractContainerConfig implements IMealTest {

    final String paramReq = "?" + "uuidMeal" + "=";
    //"THE ID CAN NOT BE NULL OR LESS THAN 0"
    private IMealDao mealDao;
    @Autowired
    private IMenuDao menuDao;
    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private MockMvc mockMvc;
    private IStudentClassDao iStudentClassDao;
    private IStudentDao iStudentDao;

    private String authorizationToken;

    private IOrderDao orderDao;

    @Autowired
    public RemoveMealTest(MockMvc mockMvc, IAdminDao adminDao, IFunctionDao functionDao, IMealDao mealDao, IStudentDao iStudentDao, IStudentClassDao iStudentClassDao, IOrderDao iOrderDao) throws Exception {
        this.mockMvc = mockMvc;
        this.adminDao = adminDao;
        this.functionDao = functionDao;
        this.mealDao = mealDao;
        this.iStudentDao = iStudentDao;
        this.iStudentClassDao = iStudentClassDao;
        this.orderDao = iOrderDao;
        cleanUp();
        initDB();
    }

    public void initDB() throws Exception {

        // create  Admin and get token
        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);


        MealEntity mealEntity = new MealEntity("platNotAvailable", "MealTest category", "MealTest description"
                , new BigDecimal("1.5"), 10, 1, MealTypeEnum.getMealTypeEnum("ENTREE"), image);
        this.mealDao.save(mealEntity);

    }

    void cleanUp() {
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();
        this.mealDao.deleteAll(); // clean  data  after  each  test
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }

    @BeforeAll
    static void copyImageTestFromTestDirectoryToImageMenuDirectory() throws IOException {
        String source = IMAGE_MEAL_TEST_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        String destination = IMAGE_MEAL_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        File sourceFile = new File(source);
        File destFile = new File(destination);
        if (!destFile.exists())
            Files.copy(sourceFile.toPath(), destFile.toPath());
    }

    @AfterAll
    static   void  removeImageOfTestIFExist (){
        String destination = IMAGE_MEAL_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        File destFile = new File(destination);
        if (destFile.exists()) {
            destFile.delete();
        }
    }


    @Test
    void removeMealTest() throws Exception {
        copyImageTestFromTestDirectoryToImageMenuDirectory();
        var mealToRemove = this.mealDao.findAll().get(0);


        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + mealToRemove.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));


        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("MealDeletedSuccessfully"))));

        Assertions.assertEquals(0, this.mealDao.findAll().size());
        var image = mealToRemove.getImage().getImagename();
        Assertions.assertFalse(new File(IMAGE_MEAL_DIRECTORY_PATH + image).exists());

    }

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
        this.menuDao.save(menuEntity);

        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + idMealToRemove.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("mealCanNotBeDeletedMenu"))));
    }


    @Test
    void removeMealInAssociationWithOrder() throws Exception {


        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        var student = AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);

        var idMealToRemove = this.mealDao.findAll().get(0);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStudent(student);
        orderEntity.setQRCode("qrcode");
        orderEntity.setPrice(new BigDecimal("2.3"));
        orderEntity.setCreationDate(LocalDate.now());
        orderEntity.setMeals(List.of(idMealToRemove));
        orderEntity.setStatus(1);
        orderEntity.setCreationTime(new Time(System.currentTimeMillis()));
        this.orderDao.save(orderEntity);

        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + idMealToRemove.getUuid())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("mealCanNotBeDeletedOrder"))));

        this.orderDao.deleteAll();
    }


    @Test
    void removeMealTestWithStudentToken() throws Exception {

        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + java.util.UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken));

        result.andExpect(status().isForbidden());

    }


    @Test
    void removeMealTestWithMealNotFound() throws Exception {

        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + java.util.UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("mealNotFound"))));

    }


    @Test
    void removeMealTestWithInvalidID() throws Exception {
        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + "ozzedoz")
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidMealUuid"))));

    }


    @Test
    void removeMealTestWithNullID() throws Exception {
        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + null)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidMealUuid"))));

    }

    @Test
    void removeMealTestWithOutId() throws Exception {
        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));


        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidMealUuid"))));

    }


    @Test
    void removeMealTestWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(post(DELETE_MEAL_URL + this.paramReq + java.util.UUID.randomUUID().toString()));
        result.andExpect(status().isUnauthorized());

    }


}
