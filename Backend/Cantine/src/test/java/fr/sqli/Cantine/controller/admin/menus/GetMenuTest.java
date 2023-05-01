package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.MenuEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

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
  @BeforeEach
  void initDatabase(){
     var meal  = IMenuTest.createMeal();
     this.mealDao.save(meal);
     var menu  = IMenuTest.createMenu(List.of(meal));
     this.menuEntity = this.menuDao.save(menu);
  }

  @AfterEach
    void cleanDatabase(){
      this.menuEntity = null;
      this.menuDao.deleteAll();
         this.mealDao.deleteAll();
    }
 /************************************* GetMenu By Id *************************************/
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
