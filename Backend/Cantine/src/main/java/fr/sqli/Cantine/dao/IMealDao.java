package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.MealEntity;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMealDao extends JpaRepository<MealEntity, Integer> {
  /* TODO:  CHANGE THE METHODE */

   @Query(value = "SELECT meal FROM MealEntity meal" +
           " WHERE (" +
           "LOWER(TRIM(meal.label)) = ?1 AND LOWER(TRIM(meal.category))= ?2 AND LOWER(TRIM(meal.description))= ?3" +
            " OR " +
           "UPPER(TRIM(meal.label)) = ?1 AND UPPER(TRIM(meal.category))= ?2 AND UPPER(TRIM(meal.description))= ?3" +
            " OR " +
              "TRIM(meal.label) = ?1 AND TRIM(meal.category)= ?2 AND TRIM(meal.description)= ?3" +
           ")")
   Optional  <MealEntity>   findByLabelAndAndCategoryAndDescriptionIgnoreCase(String label, String category, String description);
}
