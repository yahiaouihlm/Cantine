package fr.sqli.cantine.dto.in.superAdmin;

import java.math.BigDecimal;

public class TaxDtoIn {

    private BigDecimal taxValue;

    public BigDecimal getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(BigDecimal taxValue) {
        this.taxValue = taxValue;
    }
}
