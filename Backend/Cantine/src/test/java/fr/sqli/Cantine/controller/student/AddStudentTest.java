package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.StudentClassEntity;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
public class AddStudentTest  extends AbstractContainerConfig implements IStudentTest {
    @Autowired
    private IStudentClassDao studentClassDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private Environment  env;

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;

    private  MockMultipartFile imageData;
    @Autowired
    private  IStudentClassDao iStudentClassDao ;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap <String, String> formData;

    void  initDatabase() {
        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("JAVA SQLI");
        this.studentClassDao.save(studentClassEntity);
    }
    void  cleanDatabase() {
        this.studentClassDao.deleteAll();
        this.iConfirmationTokenDao.deleteAll();
        this.studentClassDao.deleteAll();
    }

    void initFormData() throws IOException {
        this.formData =new LinkedMultiValueMap<>();
        this.formData.add("firstname", "Halim");
        this.formData.add("lastname", "Yahiaoui");
        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");
        this.formData.add("password", "test33");
        this.formData.add("birthdateAsString", "2000-07-18");
        this.formData.add("town", "paris");
        this.formData.add("phone", "0631990189");
        this.formData.add("studentClass","JAVA SQLI");

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

    }


    @BeforeEach
    void  init () throws IOException {
        cleanDatabase();
        initDatabase();
        initFormData();
    }













}







