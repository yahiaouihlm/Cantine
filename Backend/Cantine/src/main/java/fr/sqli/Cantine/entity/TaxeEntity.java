package fr.sqli.Cantine.entity;


import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

/*@Entity
@Table(name="taxe")

public class TaxeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idtaxe;

    @Column(nullable=false, precision=3, scale=2)
    private BigDecimal taxe;

    public TaxeEntity() {
    }

    public Integer getIdtaxe() {
        return this.idtaxe;
    }

    public void setIdtaxe(Integer idtaxe) {
        this.idtaxe = idtaxe;
    }

    public BigDecimal getTaxe() {
        return this.taxe;
    }

    public void setTaxe(BigDecimal taxe) {
        this.taxe = taxe;
    }

}*/