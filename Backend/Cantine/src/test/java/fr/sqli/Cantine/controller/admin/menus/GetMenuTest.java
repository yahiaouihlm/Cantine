package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMenuDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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



 /************************************* GetMenu By Id *************************************/
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
