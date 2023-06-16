package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.StudentClassEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class GetStudentTest  extends AbstractContainerConfig implements IStudentTest {
    final  String paramReq = "?" + "idStudent" + "=";

    @Autowired
    private IStudentClassDao studentClassDao;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private IStudentDao studentDao;


   private StudentEntity studentEntity1;
    private  StudentEntity studentEntity2;


    void  cleanUpDb(){
        this.studentClassDao.deleteAll();
        this.studentDao.deleteAll();
    }

    @BeforeEach
    void  initDB () {
        cleanUpDb();

        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("SQLI JAVA");
        this.studentClassDao.save(studentClassEntity);

        this.studentEntity1 = IStudentTest.createStudentClassEntity("halim@social.aston-ecole.com" ,studentClassEntity);
        this.studentEntity2 = IStudentTest.createStudentClassEntity("yahiaoui@social.aston-ecole.com",studentClassEntity )  ;
    }







}
