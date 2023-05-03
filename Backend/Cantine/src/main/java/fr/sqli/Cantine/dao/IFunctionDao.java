package fr.sqli.Cantine.dao;


import fr.sqli.Cantine.entity.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFunctionDao extends JpaRepository<FunctionEntity, Integer> {
    public  FunctionEntity findByName(String name);
}
