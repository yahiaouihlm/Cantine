package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClassDao extends JpaRepository<fr.sqli.Cantine.entity.ClassEntity, Integer> {

   public Optional<ClassEntity> findByName(String name);
}
