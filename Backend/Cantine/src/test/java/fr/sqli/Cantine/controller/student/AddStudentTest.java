package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc
public class AddStudentTest  extends AbstractContainerConfig implements IStudentTest {
}
