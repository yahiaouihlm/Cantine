package fr.sqli.Cantine.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;

public class MealDtoIn implements Serializable {

    private String categorie;
    private String description;
    private String label;
    private BigDecimal prixht;
    private Integer quantite;
    private Integer status;

    private MultipartFile image;

    /**
     * Convert the MealDtoIn to a MealEntity object and return it  after checking if the meal information is valid
     * @return the MealEntity object created from the MealDtoIn object or throw an exception if the meal information is not valid
     * @throws InvalidMealInformationAdminException if the meal information is not valid
     */

    @JsonIgnore
    public MealEntity toMealEntity() throws InvalidMealInformationAdminException {
        this.checkMealInformationValidity(); // check if the meal information is valid
        MealEntity mealEntity = new MealEntity();
        mealEntity.setCategorie(this.categorie);
        mealEntity.setDescription(this.description);
        mealEntity.setLabel(this.label);
        mealEntity.setPrixht(this.prixht);
        mealEntity.setQuantite(this.quantite);
        mealEntity.setStatus(this.status);
        return mealEntity;
    }

    /**
     * Check if the meal information is valid or not and throw an exception
     * if it is not valid ( if one of the arguments is null or empty or less than 0)
     * @throws InvalidMealInformationAdminException  if the meal information is not valid
     */
    @JsonIgnore
    public void checkMealInformationValidity() throws InvalidMealInformationAdminException {
        if (this.label == null || this.label.isEmpty()) {
            throw new InvalidMealInformationAdminException("LABEL_IS_MANDATORY");
        }
        if  (this.label.length() > 100 ) {
            throw new InvalidMealInformationAdminException("LABEL_IS_TOO_LONG");
        }
        if (this.description == null || this.description.isEmpty()) {
            throw new InvalidMealInformationAdminException("DESCRIPTION_IS_MANDATORY");
        }
        if (this.description.length() > 600 ) {
            throw new InvalidMealInformationAdminException("DESCRIPTION_IS_TOO_LONG");
        }
        if (this.categorie == null || this.categorie.isEmpty()) {
            throw new InvalidMealInformationAdminException("CATEGORIES_IS_MANDATORY");
        }
        if (this.categorie.length() > 45 ) {
            throw new InvalidMealInformationAdminException("CATEGORIES_IS_TOO_LONG");
        }

        if (this.prixht == null || this.prixht.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMealInformationAdminException("PRICE_IS_MANDATORY");
        }

        if (this.quantite == null || this.quantite < 0) {
            throw new InvalidMealInformationAdminException("QUANTITY_IS_MANDATORY");
        }
        if (this.status == null || this.status < 0) {
            throw new InvalidMealInformationAdminException("STATUS_IS_MANDATORY");
        }
        if (this.image == null || this.image.isEmpty()) {
            throw new InvalidMealInformationAdminException("IMAGE_IS_MANDATORY");
        }
    }


    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPrixht() {
        return prixht;
    }

    public void setPrixht(BigDecimal prixht) {
        this.prixht = prixht;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
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
}
