package fr.sqli.cantine.controller.users.admin.menus;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import fr.sqli.cantine.entity.MenuEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMenuTest extends AbstractContainerConfig implements IMenuTest {

    final String paramReq = "?" + "uuidMenu" + "=";
    @Autowired
    private IUserDao iStudentDao;
    @Autowired
    private IStudentClassDao iStudentClassDao;
    private IMenuDao menuDao;
    private IMealDao mealDao;
    private MockMvc mockMvc;
    private IFunctionDao functionDao;
    private IUserDao adminDao;
    private String authorizationToken;
    private MenuEntity menuSaved;

    @Autowired
    public RemoveMenuTest(MockMvc mockMvc, IMenuDao menuDao, IMealDao mealDao, IFunctionDao functionDao, IUserDao adminDao) throws Exception {
        this.mockMvc = mockMvc;
        this.menuDao = menuDao;
        this.mealDao = mealDao;
        this.functionDao = functionDao;
        this.adminDao = adminDao;
        cleanDB();
        initDB();
    }

    void initDB() throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);
        var meal = this.mealDao.save(IMenuTest.createMeal());

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(IMAGE_MENU_FOR_TEST_NAME);

        MealEntity mealEntity = IMenuTest.createMealWith("MealTest2", "MealTest  description2", "MealTest  category test", new BigDecimal("10.0"), 1, 10, imageEntity);
        mealEntity.setMeal_type(MealTypeEnum.ENTREE);

        this.mealDao.save(mealEntity);

        this.menuSaved = this.menuDao.save(IMenuTest.createMenu(List.of(meal, mealEntity)));

    }

    void cleanDB() {
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }


    @Test
    void removeMenuWithExistingMenu() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq + menuSaved.getId())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(IMenuTest.responseMap.get("MenuDeletedSuccessfully"))));

        //check  if  the image  is  really   deleted
        var imageName = menuSaved.getImage().getName();
        Assertions.assertFalse(new File(DIRECTORY_IMAGE_MENU + imageName).exists());
    }


    @Test
    void removeMenuWithStudentToken() throws Exception {
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq + java.util.UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken));

        result.andExpect(status().isForbidden());

    }


    @Test
    void removeMenuWitNotFoundMenu() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq + java.util.UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isNotFound())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("MenuNotFound"))));

    }


    /***********************************  Invalid ID   ********************************************/


    @Test
    void removeMenuWithInvalidIdTest3() throws Exception {
        var result = this.mockMvc.
                perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq + "1.3")
                        .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
    }


    @Test
    void removeMenuWithInvalidIdTest2() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq + "8e5-")
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
    }

    @Test
    void removeMenuWithNullIdTest() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq + null)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
    }

    @Test
    void removeMenuWithOutIdTest() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
    }

    @Test
    void removeMenuWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(DELETE_MENU_URL + this.paramReq));
        result.andExpect(status().isUnauthorized());
    }
}
