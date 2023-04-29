package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMenuDao;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMenuTest extends AbstractContainerConfig  implements  IMenuTest{
    @Autowired
    private IMenuDao menuDao;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;

    private MockMultipartFile imageData;


    @BeforeEach
    void  initFormData() {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "Tacos");
        this.formData.add("price", "3.87");
        this.formData.add("description", "Menu  description  of Tacos menu");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
    }




}
