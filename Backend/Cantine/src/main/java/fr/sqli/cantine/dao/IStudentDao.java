package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IStudentDao  extends JpaRepository<StudentEntity, Integer> {
    Optional<StudentEntity> findByEmail(String email);
    List<StudentEntity> findByFirstnameAndLastnameAndBirthdate(String firstName , String lastName , LocalDate birthdate);
}
