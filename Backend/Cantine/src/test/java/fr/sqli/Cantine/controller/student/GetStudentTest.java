package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc
public class GetStudentTest  extends AbstractContainerConfig implements IStudentTest {
    final  String paramReq = "?" + "idStudent" + "=";



}
