package fr.sqli.Cantine.service.student;



import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.entity.StudentEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StudentService implements IStudentService {
    final  String DEFAULT_STUDENT_IMAGE;
    final  String  IMAGES_STUDENT_PATH ;
    private IStudentDao studentDao;
  private IStudentClassDao iStudentClassDao;
  private Environment environment;

  public  StudentService  (IStudentDao studentDao,  IStudentClassDao iStudentClassDao , Environment environment) {
      this.iStudentClassDao = iStudentClassDao;
      this.studentDao = studentDao;
      this.environment = environment;
        this.DEFAULT_STUDENT_IMAGE = this.environment.getProperty("sqli.cantine.default.persons.student.imagename");
        this.IMAGES_STUDENT_PATH = this.environment.getProperty("sqli.cantine.image.student.path");
  }

        @Override
        public void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, StudentClassNotFoundException {
            StudentEntity studentEntity = studentDtoIn.toStudentEntity();

            var   studentClass   =  studentDtoIn.getStudentClass();

            var  studentClassEntity   =  this.iStudentClassDao.findByName(studentClass);
            if (studentClassEntity.isEmpty()) {
                throw new StudentClassNotFoundException("INVALID STUDENT CLASS");
            }

            studentEntity.setStatus(0);
            studentEntity.setWallet(new BigDecimal(0));
            studentEntity.setStudentClass(studentClassEntity.get());
            studentEntity.setRegistrationDate(java.time.LocalDate.now());


        }



}
