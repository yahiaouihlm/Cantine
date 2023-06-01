package fr.sqli.Cantine.service.student;


import fr.sqli.Cantine.dao.IClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

  private IStudentDao studentDao;
  private IClassDao classDao;
}
