package fr.sqli.cantine.service.superAdmin;



import fr.sqli.cantine.dao.ITaxDao;
import fr.sqli.cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.cantine.dto.in.superAdmin.TaxDtoIn;
import fr.sqli.cantine.entity.TaxEntity;
import fr.sqli.cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.cantine.service.superAdmin.exception.InvalidTaxException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SuperAdminService {



    private ITaxDao iTaxDao;

    @Autowired
    public SuperAdminService( ITaxDao iTaxDao) {
        this.iTaxDao = iTaxDao;

    }


    public TaxEntity addTax(TaxDtoIn taxDtoIn) throws InvalidTaxException, ExistingTax {
        if (taxDtoIn.getTaxValue() == null || taxDtoIn.getTaxValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTaxException("TAX VALUE IS REQUIRED");
        }
        if (!this.iTaxDao.findAll().isEmpty()) {
            throw new ExistingTax("TAX VALUE ALREADY EXISTS");
        }
        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(taxDtoIn.getTaxValue());

        return this.iTaxDao.save(taxEntity);
    }


}
