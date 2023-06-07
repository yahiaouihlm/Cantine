package fr.sqli.Cantine.service.student;



import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.entity.StudentEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StudentService implements IStudentService {
  private IStudentDao studentDao;
  private IStudentClassDao iStudentClassDao;

  public  StudentService  (IStudentDao studentDao,  IStudentClassDao iStudentClassDao) {
      this.iStudentClassDao = iStudentClassDao;
    this.studentDao = studentDao;
  }

        @Override
        public void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, StudentClassNotFoundException {
            StudentEntity studentEntity = studentDtoIn.toStudentEntity();

            var   studentClass   =  studentDtoIn.getStudentClass();

            var  studentClassEntity   =  this.iStudentClassDao.findByName(studentClass);
            if (studentClassEntity.isEmpty()) {
                throw new StudentClassNotFoundException("INVALID STUDENT CLASS");
            }

            studentEntity.setS

        }



}
