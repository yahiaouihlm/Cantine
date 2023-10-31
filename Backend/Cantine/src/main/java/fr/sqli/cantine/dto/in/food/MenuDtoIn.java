package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class MenuDtoIn extends AbstractFoodDtoIn {
    private static final Logger LOG = LogManager.getLogger();


    /**
     *  the client will send  only  the ids of the meals ( check the meals id validity in the service) and the service will fetch the meals from the database
     */

    private List <String> mealUuids;

    @JsonIgnore
     public  void  toMenuEntityWithoutImage() throws InvalidFoodInformationException {
        super.CheckNullabilityAndEmptiness();
        if (description.length() > 1700) {
           throw new  InvalidFoodInformationException("DESCRIPTION_IS_TOO_LONG");
        }

        this.validateMealsUuids();
    }

    /**
     * Convert the MenuDtoIn to a MenuEntity object and return it
     * @return the MenuEntity object created from the MenuDtoIn object
     */



    @JsonIgnore
    public  void checkMenuInformationValidity() throws  InvalidFoodInformationException {
        super.CheckNullabilityAndEmptiness();
        super.checkImageValidity(this.image);
        this.validateMealsUuids();
    }



   private void validateMealsUuids ( ) throws InvalidFoodInformationException {
        if (this.getMealUuids() == null ||  this.getMealUuids().size() == 0  ) {
            MenuDtoIn.LOG.error(" THE MENU DOESN'T CONTAIN ANY MEAL ");
            throw new InvalidFoodInformationException("THE MENU DOESN'T CONTAIN ANY MEAL");
        }

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public List<String> getMealUuids() {
        return mealUuids;
    }

    public void setMealUuids(List<String> mealUuids) {
        this.mealUuids = mealUuids;
    }
}
