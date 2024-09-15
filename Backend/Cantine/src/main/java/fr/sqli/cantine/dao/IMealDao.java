package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMealDao extends JpaRepository<MealEntity, String> {
    /**
     * the  method  is  used  to  find  a  meal  by  its  label and  its  category and  its  description ignoring  the  case of  the  characters and  the  spaces
     *
     * @param label       label of the meal
     * @param category    category of the meal
     * @param description description of the meal
     * @return the meal if it exists
     */
    @Query(
            value = "SELECT meal FROM MealEntity meal WHERE (" +
                    "LOWER(REPLACE(meal.label, ' ', '')) = LOWER(REPLACE(?1, ' ', ''))" +
                    "AND LOWER(REPLACE(meal.category, ' ', '')) = LOWER(REPLACE(?2, ' ', ''))" +
                    "AND LOWER(REPLACE(meal.description, ' ', '')) = LOWER(REPLACE(?3, ' ', ''))" +
                    ")"

    )
    Optional<MealEntity> findByLabelAndAndCategoryAndDescriptionIgnoreCase(String label, String category, String description);


    @Query(
            value = "SELECT meal FROM MealEntity meal WHERE  meal.meal_type = ?1"
    )
    List<MealEntity> findAllMealsWhereTypeEqualsTo(MealTypeEnum mealType);

    @Query(value = "SELECT meal  FROM MealEntity  meal  WHERE meal.status=1")
    List<MealEntity> getAvailableMeals();

    @Query(value = "SELECT meal  FROM MealEntity  meal  WHERE meal.status=0")
    List<MealEntity> getUnavailableMeals();

    @Query(value = "SELECT meal  FROM MealEntity  meal  WHERE meal.status=2")
    List<MealEntity> getMealsInDeletionProcess();

    @Query(value = "SELECT meal  FROM MealEntity  meal  WHERE meal.id=?1")
    Optional<MealEntity> findMealById(String id);


}
