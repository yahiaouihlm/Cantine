package fr.sqli.Cantine.service.student;


import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.service.admin.adminDashboard.account.AdminService;
import fr.sqli.Cantine.service.images.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

@ExtendWith(MockitoExtension.class)
public class GetStudentTest {
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";
    @Mock
     private IStudentDao  studentDao;
    @Mock
    private ImageService imageService;
    private IConfirmationTokenDao iConfirmationToken;
    @Mock
    private IStudentClassDao studentClassDao;
    @Mock
    private MockEnvironment environment;
    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void  setUp (){
        this.studentService = new StudentService(studentDao, studentClassDao,  this.environment,  null ,  imageService);

    }

}
