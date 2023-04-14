package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.MealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMealDao extends JpaRepository<MealEntity, Integer> {
  /* TODO:  CHANGE THE METHODE */
    @Query("SELECT meal FROM MealEntity meal  " +
            "WHERE (LOWER(TRIM(meal.label)) = ?1   OR     TRIM(LOWER(meal.label))=?1  )AND " +
            "(LOWER(TRIM(meal.category)) = ?2      OR    TRIM(LOWER(meal.category))=?2  )AND " +
            "LOWER(TRIM(meal.description)) = ?3   OR    TRIM(LOWER(meal.description))=?3 "
    )
     List<MealEntity> existsBy(String label, String category, String description);
}
