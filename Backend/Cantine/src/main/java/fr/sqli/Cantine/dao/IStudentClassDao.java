package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.StudentClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStudentClassDao  extends JpaRepository <fr.sqli.Cantine.entity.StudentClassEntity, Integer>{


    public Optional<StudentClassEntity> findByName(String name);
}
