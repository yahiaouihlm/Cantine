package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.StudentClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStudentClassDao extends JpaRepository<fr.sqli.cantine.entity.StudentClassEntity, String> {
    public Optional<StudentClassEntity> findByName(String name);
}
