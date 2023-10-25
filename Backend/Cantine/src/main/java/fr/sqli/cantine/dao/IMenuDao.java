package fr.sqli.cantine.dao;


import fr.sqli.cantine.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface IMenuDao extends JpaRepository<MenuEntity, Integer> {





    @Query(value = "SELECT menu FROM MenuEntity menu WHERE (" +
            "LOWER(REPLACE(menu.label, ' ', '')) = LOWER(REPLACE(?1, ' ', ''))" +
            "AND LOWER(REPLACE(menu.description, ' ', '')) = LOWER(REPLACE(?2, ' ', ''))" +
             "AND menu.price = ?3 "+
            ")"

    )
    Optional<MenuEntity> findByLabelAndAndPriceAndDescriptionIgnoreCase(String label, String description , BigDecimal price);



}
