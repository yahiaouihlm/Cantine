package fr.sqli.Cantine.dao;


import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface IMenuDao extends JpaRepository<MenuEntity, Integer> {

    @Query(value = "SELECT meal FROM MealEntity meal WHERE (" +
            "LOWER(REPLACE(meal.label, ' ', '')) = LOWER(REPLACE(?1, ' ', ''))" +
            "AND LOWER(REPLACE(meal.description, ' ', '')) = LOWER(REPLACE(?2, ' ', ''))" +
             "AND meal.price = ?3 "+
            ")"

    )
    Optional<MenuEntity> findByLabelAndAndPriceAndDescriptionIgnoreCase(String label, String description , BigDecimal price);


}
