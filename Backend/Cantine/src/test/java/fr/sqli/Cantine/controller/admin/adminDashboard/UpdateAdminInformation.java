package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateAdminInformation  extends AbstractContainerConfig implements  IAdminTest {
    @Autowired
    private IFunctionDao functionDao;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Environment environment;

    private MockMultipartFile imageData;
    private MultiValueMap<String, String> formData;
    private FunctionEntity savedFunction;
    private AdminEntity savedAdmin;


}
