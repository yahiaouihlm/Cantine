package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMenuDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMenuTest extends AbstractContainerConfig implements IMenuTest {

    final String paramReq = "?"+"idMenu" + "=";
    @Autowired
    private IMenuDao menuDao;

    @Autowired
    private MockMvc mockMvc;


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
