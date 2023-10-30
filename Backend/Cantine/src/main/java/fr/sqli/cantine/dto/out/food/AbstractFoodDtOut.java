package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.ImageEntity;

import java.math.BigDecimal;

public abstract class AbstractFoodDtOut {
    protected final String uuid;
    protected final String label;
    protected final String description;

    protected final BigDecimal price;
    protected final Integer quantity;
    protected final Integer status;

    protected final String image;

    public AbstractFoodDtOut(String uuid, String label, String description, BigDecimal price, Integer quantity, Integer status, ImageEntity foodImageEntity, String foodUrlImage) {
        this.uuid = uuid;
        this.label = label;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.image = foodUrlImage + foodImageEntity.getImagename();

    }
}
