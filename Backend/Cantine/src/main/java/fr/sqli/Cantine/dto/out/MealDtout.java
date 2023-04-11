package fr.sqli.Cantine.dto.out;

import fr.sqli.Cantine.entity.MealEntity;

import java.math.BigDecimal;

public class MealDtout {

    private  final   Integer id ;
    private  final String label ;
    private final String description;
    private  final  String categorie;
    private  final   BigDecimal prixht;
    private final  Integer quantite;
    private final Integer status;

    private final String image ;

    


    public MealDtout(MealEntity meal ,  String mealUrlImage) {
        this.id = meal.getIdplat();
        this.description = meal.getDescription();
        this.categorie = meal.getCategorie();
        this.prixht = meal.getPrixht();
        this.quantite = meal.getQuantite();
        this.status = meal.getStatus();
        this.label = meal.getLabel();
        var  path  = meal.getImage().getImagename();
        this.image =mealUrlImage + path;
    }



    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getCategorie() {
        return categorie;
    }

    public BigDecimal getPrixht() {
        return prixht;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public Integer getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
