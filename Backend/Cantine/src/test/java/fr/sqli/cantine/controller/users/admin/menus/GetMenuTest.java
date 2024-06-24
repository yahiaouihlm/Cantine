
package fr.sqli.cantine.controller.users.admin.menus;

import com.auth0.jwt.interfaces.Header;
import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import fr.sqli.cantine.entity.MenuEntity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class GetMenuTest extends AbstractContainerConfig implements IMenuTest {

    private final String paramReq = "?" + "uuidMenu" + "=";

    private IMenuDao menuDao;
    private MockMvc mockMvc;
    private IMealDao mealDao;
    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private String authorizationToken;
    private MenuEntity menuSaved;


    @Autowired
    public GetMenuTest(IAdminDao adminDao, IMenuDao menuDao, IMealDao mealDao, MockMvc mockMvc, IFunctionDao functionDao) throws Exception {
        this.adminDao = adminDao;
        this.menuDao = menuDao;
        this.mealDao = mealDao;
        this.mockMvc = mockMvc;
        this.functionDao = functionDao;
        cleanDatabase();
        initDatabase();

    }

    void initDatabase() throws Exception {
        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);
        var meal = this.mealDao.save(IMenuTest.createMeal());

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_MENU_FOR_TEST_NAME);

        MealEntity mealEntity = IMenuTest.createMealWith("MealTest2", "MealTest  description2", "MealTest  category test", new BigDecimal(10.0), 1, 10, imageEntity);
        mealEntity.setMealType(MealTypeEnum.ENTREE);

        this.mealDao.save(mealEntity);

        this.menuSaved = this.menuDao.save(IMenuTest.createMenu(List.of(meal, mealEntity)));
    }

    void cleanDatabase() {
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();

    }




    @Test
    @Rollback(value = true)
    void  getAllMenusInProcessOfDeletion() throws Exception {
         this.menuSaved.setStatus(2);
         this.menuDao.save(this.menuSaved);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONLY_MENUS_IN_DELETION_PROCESS_URL)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].label").value(CoreMatchers.is(this.menuSaved.getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.is(this.menuSaved.getDescription())));
    }



/************************************** Get All Menus *************************************/


  @Test
  void  getAllMenusWithOneMenuDatabase() throws Exception {

      var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_MENUS_URL)
              .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
      result.andExpect(MockMvcResultMatchers.status().isOk());
      result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(1)));
      result.andExpect(MockMvcResultMatchers.jsonPath("$[0].label").value(CoreMatchers.is(this.menuSaved.getLabel())));
      result.andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.is(this.menuSaved.getDescription())));
  }



    /************************************* GetMenu By Id *************************************/


    @Test
    void getMenuByIdTestWithStudentAuth () throws Exception {
        var  mealUuid= this.menuSaved.getUuid(); // id must be not exist in database
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq +mealUuid )
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect( status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.uuid", CoreMatchers.is(mealUuid)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.label", CoreMatchers.is(this.menuSaved.getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(this.menuSaved.getDescription())));
    }



 @Test
 void getMenuByIdWithInvalidId () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq + "1.2" )
             .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
     result.andExpect( status().isBadRequest())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
 }

 @Test
 void getMenuByIdWithInvalidUUd () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders
             .get(GET_ONE_MENU_URL+ this.paramReq + java.util.UUID.randomUUID().toString()   )
             .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

     result.andExpect( status().isNotFound())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("MenuNotFound"))));
 }
 @Test
 void getMenuByIdWithNullId () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders
             .get(GET_ONE_MENU_URL+ this.paramReq + null )
             .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

     result.andExpect( status().isBadRequest())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
 }

    @Test
    void getMenuByIdWithOutID() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders
                .get(GET_ONE_MENU_URL + this.paramReq)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidMenuID"))));
    }

    @Test
    void getMenuByIdWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL + this.paramReq));
        result.andExpect(status().isUnauthorized());

    }

}
