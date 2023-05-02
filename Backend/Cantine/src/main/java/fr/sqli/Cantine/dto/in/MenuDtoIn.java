package fr.sqli.Cantine.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class MenuDtoIn extends  AbstractDtoIn {


    private String label;


    private String description;

    private BigDecimal price;


    private Integer status;

    private MultipartFile image;
    private Integer quantity;

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
     @JsonIgnore
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
     public  MenuEntity toMenuEntityWithoutImage() throws InvalidMenuInformationException, InvalidMealInformationException {
        super.checkValidity(MenuEntity.class, this.label, this.description, this.price, this.status, this.quantity, null);
        return this.createMenuEntity();
     }

    /**
     * Convert the MenuDtoIn to a MenuEntity object and return it
     * @return the MenuEntity object created from the MenuDtoIn object
     */
    @JsonIgnore
    private MenuEntity createMenuEntity() {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setLabel(this.label.trim());
        menuEntity.setDescription(this.description.trim());
        menuEntity.setPrice(this.price);
        menuEntity.setStatus(this.status);
        menuEntity.setQuantity(this.quantity);
        return menuEntity;
    }

    /**
     * Check if the menu information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     *       the image is also checked
     * @throws InvalidMealInformationException it's  never thrown because it's a menu
     * @throws InvalidMenuInformationException if the menu information is not valid ( if one of the arguments is null or empty or less than 0)
     */
    @JsonIgnore
    private  void checkMenuInformationValidity() throws InvalidMealInformationException, InvalidMenuInformationException {
        super.checkValidity( MenuEntity.class, this.label,  this.description, this.price, this.status ,  this.quantity, null);
        super.checkImageValidity( MenuEntity.class ,   this.image);
    }


    @JsonIgnore
    public  List<Integer> fromStringMealIDsToIntegerMealIDs(){
        return this.getMealIDs().stream().map((id)-> id.replaceAll("[^0-9]+", "")).map(
                Integer::parseInt).toList();

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
