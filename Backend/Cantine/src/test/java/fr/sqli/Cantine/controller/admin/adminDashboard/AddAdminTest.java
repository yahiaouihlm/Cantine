package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@SpringBootTest
@AutoConfigureMockMvc
public class AddAdminTest  extends AbstractContainerConfig  implements  IAdminTest {

    @Autowired
    private IFunctionDao functionDao;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;
    private FunctionEntity savedFunction;
    void initDabase() {
        FunctionEntity function = new FunctionEntity();
        function.setName("Manager");
        this.savedFunction = this.functionDao.save(function);
    }
    void initFormData() {
        this.formData =new LinkedMultiValueMap<>();
        this.formData.add("firstname", "halim");
        this.formData.add("lastname", "yahiaoui");
        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");
        this.formData.add("password", "test33");
        this.formData.add("birthdateAsString", "2000-07-18");
        this.formData.add("town", "paris");
        this.formData.add("address", "102  rue de cheret 75013 paris");
        this.formData.add("phone", "0631990189");
        this.formData.add("function","Manager");

    }



}
