package fr.sqli.Cantine.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IStudentClassDao  extends JpaRepository <fr.sqli.Cantine.entity.StudentClassEntity, Integer>{
}
