package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.ImageEntity;

import java.math.BigDecimal;

public abstract class AbstractFoodDtOut {
    protected final String id;
    protected final String label;
    protected final String description;

    protected final BigDecimal price;
    protected final Integer quantity;
    protected final Integer status;

    protected final String image;

    public AbstractFoodDtOut(String uuid, String label, String description, BigDecimal price, Integer quantity, Integer status, ImageEntity foodImageEntity, String foodUrlImage) {
        this.id = uuid;
        this.label = label;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.image = foodUrlImage + foodImageEntity.getName();

    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
