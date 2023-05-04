package fr.sqli.Cantine.service.superAdmin;


import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminService {

    private IFunctionDao iFunctionDao;

    @Autowired
    public  SuperAdminService(IFunctionDao iFunctionDao){
        this.iFunctionDao = iFunctionDao;
    }


    public FunctionEntity  addFunction (FunctionDtoIn functionDtoIn) throws InvalidPersonInformationException {
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setName(functionDtoIn.getName());
        return  this.iFunctionDao.save(functionEntity);
    }


}
