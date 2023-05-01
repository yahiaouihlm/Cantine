package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMenuDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMenuTest extends AbstractContainerConfig implements IMenuTest {

    @Autowired
    private IMenuDao menuDao;

    @Autowired
    private MockMvc mockMvc;


   @Test
  void removeMenuWithOutIdTest() {

  }

}
