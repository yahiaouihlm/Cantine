package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.MenuEntity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class GetMenuTest  extends AbstractContainerConfig implements  IMenuTest {

  private  final  String paramReq  =  "?"+"idMenu" + "=";

  @Autowired
  private IMenuDao menuDao;

  @Autowired
  private MockMvc mockMvc;

  private MenuEntity menuEntity;
 @Autowired
 private IMealDao mealDao;

  void initDatabase(){
     var meal  = IMenuTest.createMeal();
     this.mealDao.save(meal);
     var menu  = IMenuTest.createMenu(List.of(meal));
     this.menuEntity = this.menuDao.save(menu);
  }

    void cleanDatabase(){
      this.menuEntity = null;
      this.menuDao.deleteAll();
         this.mealDao.deleteAll();

  }


  @BeforeEach
  void init (){
      cleanDatabase();
      initDatabase();
  }
  /************************************** Get All Menus *************************************/

  @Test
  void  getAllMenusWithOneMenuDatabase() throws Exception {

      var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_MENUS_URL));
      result.andExpect(MockMvcResultMatchers.status().isOk());
      result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(1)));
      result.andExpect(MockMvcResultMatchers.jsonPath("$[0].label").value(CoreMatchers.is(this.menuEntity.getLabel())));
      result.andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.is(this.menuEntity.getDescription())));
  }
  @Test
  void  getAllMenusWithEmptyDatabase() throws Exception {
      this.menuDao.deleteAll();
      var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_MENUS_URL));
      result.andExpect(MockMvcResultMatchers.status().isOk());
      result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(0)));
  }


 /************************************* GetMenu By Id *************************************/



 @Test
 void getMenuByIdTest () throws Exception {
     var  idMeal= this.menuEntity.getId(); // id must be not exist in database
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq +idMeal ));

     result.andExpect( status().isOk());
    result.andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(idMeal)));
     result.andExpect(MockMvcResultMatchers.jsonPath("$.label", CoreMatchers.is(this.menuEntity.getLabel())));
     result.andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(this.menuEntity.getDescription())));
 }


    @Test
 void getMenuByIdWithOutMenu () throws Exception {
     var  idMeal= this.menuEntity.getId() + 3562; // id must be not exist in database
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq +idMeal ));
     result.andExpect( status().isNotFound())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("MenuNotFound"))));
 }

 @Test
 void getMenuByIdWithInvalidId2 () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq + "54fd" ));
     result.andExpect( status().isNotAcceptable())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
 }
 @Test
 void getMenuByIdWithInvalidId () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq + "1.2" ));
     result.andExpect( status().isNotAcceptable())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
 }
 @Test
 void getMenuByIdWithNegativeId () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq + "-1" ));
     result.andExpect( status().isBadRequest())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidParameter"))));
 }
 @Test
 void getMenuByIdWithNullId () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq + null ));
     result.andExpect( status().isNotAcceptable())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
 }
 @Test
 void getMenuByIdWithOutID () throws Exception {
     var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MENU_URL+ this.paramReq));
     result.andExpect( status().isNotAcceptable())
             .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("missingParam"))));
 }




}
