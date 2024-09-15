package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public abstract class AbstractFoodDtoIn {

    private String id;
    private String label;
    private String description;
    private BigDecimal price;
    private Integer status;
    private Integer quantity;
    private MultipartFile image;

    @JsonIgnore
    public void CheckNullabilityAndEmptiness() throws InvalidFoodInformationException {

        //------------- Label ----------------//
        if (this.label == null || label.isBlank()) {
            throw new InvalidFoodInformationException("LABEL_IS_MANDATORY");
        } else if (this.label.length() > 100) {
            throw new InvalidFoodInformationException("LABEL_IS_TOO_LONG");
        } else if (this.removeSpaces(this.label).length() < 3) {
            throw new InvalidFoodInformationException("LABEL_IS_TOO_SHORT");
        }

        //------------- Description ----------------//
        else if (this.description == null || this.description.isBlank()) {
            throw new InvalidFoodInformationException("DESCRIPTION_IS_MANDATORY");
        } else if (this.removeSpaces(this.description).length() < 5) {
            throw new InvalidFoodInformationException("DESCRIPTION_IS_TOO_SHORT");
        } else if (this.description.length() > 3000) {
            throw new InvalidFoodInformationException("DESCRIPTION_IS_TOO_LONG");
        }


        //------------- Price ----------------//
        else if (this.price == null) {
            throw new InvalidFoodInformationException("PRICE_IS_MANDATORY");
        } else if (this.price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFoodInformationException("PRICE MUST BE GREATER THAN 0");
        } else if (this.price.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw new InvalidFoodInformationException("PRICE MUST BE LESS THAN 1000");
        }


        //------------- status  ----------------//
        else if (this.status == null) {
            throw new InvalidFoodInformationException("STATUS_IS_MANDATORY");
        } else if (this.status != 0 && this.status != 1) {
            throw new InvalidFoodInformationException("STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE");
        }


        //------------- quantity ----------------//
        else if (this.quantity == null) {
            throw new InvalidFoodInformationException("QUANTITY_IS_MANDATORY");
        } else if (this.quantity < 0) {
            throw new InvalidFoodInformationException("QUANTITY MUST BE GREATER THAN 0");
        } else if (quantity > 10000) {
            throw new InvalidFoodInformationException("QUANTITY_IS_TOO_HIGH");
        }


    }

    /**
     * remove all the spaces in a string
     *
     * @param str the string to remove the spaces
     * @return the string without spaces
     */

    @JsonIgnore
    public String removeSpaces(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("\\s+", "");
    }

    @JsonIgnore
    public void checkImageValidity() throws InvalidFoodInformationException {
        if (this.image == null || this.image.isEmpty() || this.image.getSize() == 0) {
            throw new InvalidFoodInformationException("IMAGE_IS_MANDATORY");
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}

