package fr.sqli.Cantine.service.superAdmin;


import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dao.ITaxDao;
import fr.sqli.Cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.entity.TaxEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.Cantine.service.superAdmin.exception.InvalidTaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SuperAdminService {

    private IFunctionDao iFunctionDao;
    private ITaxDao iTaxDao;
    @Autowired
    public  SuperAdminService(IFunctionDao iFunctionDao){
        this.iFunctionDao = iFunctionDao;
    }


    public FunctionEntity  addFunction (FunctionDtoIn functionDtoIn) throws InvalidPersonInformationException {
        var  functionName  =  functionDtoIn.getName();
        var  functionEntity = this.iFunctionDao.findByName(functionName);
        if  (functionEntity.isEmpty()) {
            throw  new  InvalidPersonInformationException("FUNCTION NAME ALREADY EXISTS");
        }
        FunctionEntity newfunctionEntity = new FunctionEntity();
        newfunctionEntity.setName(functionDtoIn.getName());
        return  this.iFunctionDao.save(newfunctionEntity);
    }


    public TaxEntity addTax(BigDecimal taxValue) throws InvalidTaxException, ExistingTax {
        if (taxValue == null || taxValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTaxException("TAX VALUE IS REQUIRED");
        }
        if  ((long) this.iTaxDao.findAll().size() > 0) {
            throw  new ExistingTax("TAX VALUE ALREADY EXISTS");
        }
        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(taxValue);

        return this.iTaxDao.save(taxEntity);
    }

}
