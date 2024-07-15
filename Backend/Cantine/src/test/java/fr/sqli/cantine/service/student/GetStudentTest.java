package fr.sqli.cantine.service.student;


import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.*;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import fr.sqli.cantine.service.users.student.Impl.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GetStudentTest {
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";
    @Mock
     private IStudentDao  studentDao;
    @Mock
    private ImageService imageService;

    private String  IMAGE_URL = "http://localhost:8080/cantine/download/images/persons/students/";
    @Mock
    private IStudentClassDao studentClassDao;
    @Mock
    private MockEnvironment environment;
    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void  setUp (){
        this.studentService = new StudentService(studentDao, studentClassDao,  this.environment,  null ,  imageService ,  null);

    }

    @Test
    void  getAdminByIDTest () throws InvalidUserInformationException, UserNotFoundException {
        var uuid = java.util.UUID.randomUUID().toString();
       // this.environment.setProperty("sqli.cantine.images.url.student", "src/test/resources/imagesTests");
        Mockito.when(this.environment.getProperty("sqli.cantine.images.url.student")).thenReturn(this.IMAGE_URL);
        StudentClassEntity studentClass = new StudentClassEntity();
        studentClass.setId(1);
        studentClass.setName("test-studentClass");

        StudentEntity student = new StudentEntity();
        student.setId(1);
        student.setUuid(uuid);
        student.setFirstname("firstname");
        student.setLastname("lastname");
        student.setEmail("email@test.fr");
        student.setBirthdate(LocalDate.now());
        student.setTown("town");
        student.setPhone("0631990180");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_TESTS_PATH);
        student.setImage(imageEntity);
        student.setStudentClass(studentClass);
        Mockito.when(this.studentDao.findByUuid(student.getUuid())).thenReturn(Optional.of(student));

        var  rsult =  this.studentService.getStudentByUuid(uuid);

        System.out.println(rsult.getImage());
        Assertions.assertEquals(rsult.getId(), student.getUuid());
        Assertions.assertEquals(rsult.getFirstname(), student.getFirstname());
        Assertions.assertEquals(rsult.getLastname(), student.getLastname());
        Assertions.assertEquals(rsult.getEmail(), student.getEmail());
        Assertions.assertEquals(rsult.getBirthdate(), student.getBirthdate());
        Assertions.assertEquals(rsult.getTown(), student.getTown());
        Assertions.assertEquals(rsult.getPhone(), student.getPhone());
        Assertions.assertEquals(rsult.getImage(), this.IMAGE_URL + student.getImage().getImagename());


    }
    @Test
    void  getAdminByIdWithNotFoundAdmin () throws InvalidUserInformationException {
        String studentUuid = java.util.UUID.randomUUID().toString();
        Mockito.when(this.studentDao.findByUuid(studentUuid)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.studentService.getStudentByUuid(studentUuid);
        });


    }


    @Test
    void getAdminByIdWithInvalidUuID (){
        String studentUuid = "null";
        assertThrows(InvalidUserInformationException.class, () -> {
            this.studentService.getStudentByUuid(studentUuid);
        });
    }
    @Test
    void getAdminByIdWithNullUuID (){

        assertThrows(InvalidUserInformationException.class, () -> {
            this.studentService.getStudentByUuid(null);
        });
    }






}
