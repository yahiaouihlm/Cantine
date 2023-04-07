package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.MealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IMealDao  extends JpaRepository<MealEntity, Integer>  {

}
