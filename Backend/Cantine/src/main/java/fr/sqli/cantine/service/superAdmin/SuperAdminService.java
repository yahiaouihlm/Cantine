package fr.sqli.cantine.service.superAdmin;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.dao.ITaxDao;
import fr.sqli.cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.cantine.dto.in.superAdmin.TaxDtoIn;
import fr.sqli.cantine.entity.FunctionEntity;
import fr.sqli.cantine.entity.TaxEntity;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.cantine.service.superAdmin.exception.ExistingUserByEmail;
import fr.sqli.cantine.service.superAdmin.exception.InvalidTaxException;
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


    public FunctionEntity  addFunction (FunctionDtoIn functionDtoIn) throws InvalidUserInformationException {
        var  functionName  =  functionDtoIn.getName();
        var  functionEntity = this.iFunctionDao.findByName(functionName);
        if  (functionEntity.isEmpty()) {
            throw  new InvalidUserInformationException("FUNCTION NAME ALREADY EXISTS");
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
            throw new ExistingUserByEmail("EMAIL ALREADY EXISTS");
        }
        if (this.iAdminDao.findByEmail(email).isPresent() || this.iStudentDao.findByEmail(email).isPresent()) {
            throw new ExistingUserByEmail("EMAIL ALREADY EXISTS");
        }

    }
}
