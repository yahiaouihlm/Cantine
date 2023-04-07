package fr.sqli.Cantine.dto.out;

import fr.sqli.Cantine.entity.MealEntity;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class MealDtout {

    private  Integer id ;
    private String label ;
    private String description;
    private String categorie;
    private BigDecimal prixht;
    private Integer quantite;
    private Integer status;

    private String image ;


    public MealDtout(MealEntity meal ,  String mealUrl) {
        this.id = meal.getIdplat();
        this.description = meal.getDescription();
        this.categorie = meal.getCategorie();
        this.prixht = meal.getPrixht();
        this.quantite = meal.getQuantite();
        this.status = meal.getStatus();
        this.label = meal.getLabel();
        var  path  = meal.getImage().getImagename();
        this.image =mealUrl + path;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
