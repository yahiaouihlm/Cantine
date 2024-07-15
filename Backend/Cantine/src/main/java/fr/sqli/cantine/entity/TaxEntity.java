package fr.sqli.cantine.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "tax")
public class TaxEntity extends AbstractEntity implements Serializable {

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal tax;


}