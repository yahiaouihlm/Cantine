package fr.sqli.Cantine.service.superAdmin;


import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminService {

    private IFunctionDao iFunctionDao;

    @Autowired
    public  SuperAdminService(IFunctionDao iFunctionDao){
        this.iFunctionDao = iFunctionDao;
    }


    public FunctionEntity  addFunction (  Fun){
        return  null;
    }


}
