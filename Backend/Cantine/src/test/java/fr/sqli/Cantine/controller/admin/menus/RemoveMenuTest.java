package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMenuTest extends AbstractContainerConfig implements IMenuTest {

    final String paramReq = "?"+"idMenu" + "=";
    final  String MENU_DELETED_SUCCESSFULLY  =  "MENU DELETED SUCCESSFULLY";
    @Autowired
    private IMenuDao menuDao;

    @Autowired
    private IMealDao mealDao;
    @Autowired
    private MockMvc mockMvc;

    MenuEntity initDataBase(){
        var meal  =  IMenuTest.createMeal();

        this.mealDao.save(meal);
        var menu  = IMenuTest.createMenu(List.of(meal));
        return this.menuDao.save(menu);

    }
    @AfterEach
    void  cleanDataBase(){
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
    }
    @Test
    void removeMenuWithExistingMenu() throws Exception {

        var  menuSaved  =  this.initDataBase(); // save  menu  in  database



        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+menuSaved.getId()));
        result.andExpect( status().isOk())
                .andExpect(content().string(MENU_DELETED_SUCCESSFULLY));

          //check  if  the image  is  really   deleted
       var imageName = menuSaved.getImage().getImagename();
        Assertions.assertTrue(!new File(DIRECTORY_IMAGE_MENU+imageName).exists());
    }

    @Test
    void  removeMenuWithNotExistingMenu() throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+"100"));

        result.andExpect( status().isNotFound())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("MenuNotFound"))));

    }


    /***********************************  Invalid ID   ********************************************/
    @Test
    void  removeMenuWithNegativeIdTest() throws Exception {
        var  result  =   this.mockMvc.
                perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+"-4" ));
        result.andExpect( status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidParameter"))));
    }
    @Test
    void  removeMenuWithInvalidIdTest3() throws Exception {
        var  result  =   this.mockMvc.
                perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+"1.3" ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }
    @Test
    void  removeMenuWithInvalidIdTest2() throws Exception {
        var  result  =   this.mockMvc.
                perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+"8e5-" ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }
    @Test
    void  removeMenuWithInvalidIdTest() throws Exception {
        var  result  =   this.mockMvc.
                perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+"INVALID_ID" ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }
    @Test
    void removeMenuWithNullIdTest() throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+this.paramReq+null ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
   void removeMenuWithOutIdTest() throws Exception {
      var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_MENU_URL+ this.paramReq));
               result.andExpect( status().isNotAcceptable())
               .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("missingParam"))));
  }

}
