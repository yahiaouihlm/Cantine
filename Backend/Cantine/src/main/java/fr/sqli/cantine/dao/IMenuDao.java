package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IMenuDao extends JpaRepository<MenuEntity, String> {


    @Query(value = "SELECT menu FROM MenuEntity menu WHERE (" +
            "LOWER(REPLACE(menu.label, ' ', '')) = LOWER(REPLACE(?1, ' ', ''))" +
            "AND LOWER(REPLACE(menu.description, ' ', '')) = LOWER(REPLACE(?2, ' ', ''))" +
            "AND menu.price = ?3 " +
            ")"

    )
    Optional<MenuEntity> findByLabelAndAndPriceAndDescriptionIgnoreCase(String label, String description, BigDecimal price);

    @Query(value = "SELECT DISTINCT menu.label FROM  MenuEntity  menu WHERE LOWER(menu.label) LIKE LOWER(CONCAT('%',?1,'%'))")
    List<String> findLabelsContainsIgnoreCase(String term);

    @Query(value = " SELECT menu FROM MenuEntity  menu  WHERE menu.status=1 ")
    List<MenuEntity> getAvailableMenus();

    @Query(value = "SELECT menu  FROM MenuEntity  menu  WHERE menu.status=0")
    List<MenuEntity> getUnavailableMenu();

    @Query(value = "SELECT menu  FROM MenuEntity  menu  WHERE menu.status=2")
    List<MenuEntity> getMenusInDeletionProcess();

    @Query(value = "SELECT menu  FROM MenuEntity  menu  WHERE menu.id=?1")
    Optional<MenuEntity> findMenuById(String id);


    List<MenuEntity> findMenuEntitiesByLabel(String label);


}
