package fr.sqli.Cantine.service.superAdmin;


import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dao.ITaxDao;
import fr.sqli.Cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.Cantine.dto.in.superAdmin.TaxDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.entity.TaxEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.Cantine.service.superAdmin.exception.ExistingUserByEmail;
import fr.sqli.Cantine.service.superAdmin.exception.InvalidTaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SuperAdminService {

    private IAdminDao  iAdminDao;
    private IStudentDao iStudentDao;
    private IFunctionDao iFunctionDao;
    private ITaxDao iTaxDao;
    @Autowired
    public  SuperAdminService(IFunctionDao iFunctionDao , ITaxDao iTaxDao ,  IAdminDao iAdminDao , IStudentDao iStudentDao) {
        this.iFunctionDao = iFunctionDao;
        this.iTaxDao = iTaxDao;
        this.iAdminDao = iAdminDao;
        this.iStudentDao = iStudentDao;
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


    public TaxEntity addTax(TaxDtoIn taxDtoIn) throws InvalidTaxException, ExistingTax {
        if (taxDtoIn.getTaxValue() == null || taxDtoIn.getTaxValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTaxException("TAX VALUE IS REQUIRED");
        }
        if  (this.iTaxDao.findAll().size() > 0){
            throw  new ExistingTax("TAX VALUE ALREADY EXISTS");
        }
        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(taxDtoIn.getTaxValue());

        return this.iTaxDao.save(taxEntity);
    }


    public void ExistingEmail(String email) throws ExistingUserByEmail {

        if (email == null || email.isEmpty()) {
            throw new ExistingUserByEmail("EMAIL IS REQUIRED");
        }
        if (this.iAdminDao.findByEmail(email).isPresent() || this.iStudentDao.findByEmail(email).isPresent()) {
            throw new ExistingUserByEmail("EMAIL ALREADY EXISTS");
        }

    }
}
