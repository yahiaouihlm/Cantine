package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class MenuDtoIn extends AbstractDtoIn {

    private Integer menuId;

    private String label;


    private String description;

    private BigDecimal price;


    private Integer status;

    private Integer quantity;
    private MultipartFile image;

    /**
     *  the client will send  only  the ids of the meals ( check the meals id validity in the service) and the service will fetch the meals from the database
     */

    private List <String> mealIDs;

    /**
     * Convert the MenuDtoIn to a MenuEntity object and return it after checking if the menu information is valid
     * @return the MenuEntity object created from the MenuDtoIn object or throw an exception if the menu information is not valid
     * @throws InvalidMenuInformationException if the menu information is not valid ( if one of the arguments is null or empty or less than 0)
     * @throws InvalidMealInformationException it's never thrown because it's a menu
     */
   /*  @JsonIgnore
    public MenuEntity  toMenuEntity() throws InvalidMenuInformationException, InvalidMealInformationException {
        this.checkMenuInformationValidity();
        return this.createMenuEntity();
    }

    /**
     * Convert the MenuDtoIn to a MenuEntity object and return it after checking if the menu information is valid except the image
     * @return the MenuEntity object created from the MenuDtoIn object or throw an exception if the menu information is not valid
     * @throws InvalidMenuInformationException if the menu information is not valid ( if one of the arguments is null or empty or less than 0)
     * @throws InvalidMealInformationException it's never thrown because it's a menu
     */
    @JsonIgnore
     public  void  toMenuEntityWithoutImage() throws InvalidMenuInformationException, InvalidMealInformationException {
        super.checkValidity(MenuEntity.class, this.label, this.description, this.price, this.status, this.quantity, null);
     }

    /**
     * Convert the MenuDtoIn to a MenuEntity object and return it
     * @return the MenuEntity object created from the MenuDtoIn object
     */


    /**
     * Check if the menu information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     *       the image is also checked
     * @throws InvalidMealInformationException it's  never thrown because it's a menu
     * @throws InvalidMenuInformationException if the menu information is not valid ( if one of the arguments is null or empty or less than 0)
     */
    @JsonIgnore
    public  void checkMenuInformationValidity() throws InvalidMealInformationException, InvalidMenuInformationException {
        super.checkValidity( MenuEntity.class, this.label,  this.description, this.price, this.status ,  this.quantity, null);
        super.checkImageValidity( MenuEntity.class ,   this.image);
    }


    @JsonIgnore
    public  List<Integer> fromStringMealIDsToIntegerMealIDs() throws  InvalidMenuInformationException{
        try {
            return this.getMealIDs().stream().map((id)-> id.replaceAll("[^0-9]+", "")).map(
                    Integer::parseInt).toList();
        }
        catch (NumberFormatException e){
            throw new InvalidMenuInformationException("INVALID  MEALS IDS");
        }
    }


    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<String> getMealIDs() {
        return mealIDs;
    }

    public void setMealIDs(List<String> mealIDs) {
        this.mealIDs = mealIDs;
    }
}
