package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStudentDao  extends JpaRepository<StudentEntity, Integer> {
    Optional<StudentEntity> findByEmail(String email);
}
