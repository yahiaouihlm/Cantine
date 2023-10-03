package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IStudentDao  extends JpaRepository<StudentEntity, Integer> {

    @Query(value = "SELECT student FROM StudentEntity student WHERE (" +
            "LOWER(REPLACE(student.firstname, ' ', '')) = LOWER(REPLACE(?1, ' ', ''))" +
            "AND LOWER(REPLACE(student.lastname, ' ', '')) = LOWER(REPLACE(?2, ' ', ''))" +

            ")"

    )

    List<StudentEntity> findByFirstnameAndLastnameAndBirthdate(String firstName , String lastName , LocalDate birthdate);
    Optional<StudentEntity> findByEmail(String email);
}


//  "AND  student.birthdate  =  ?3 " +