package fr.sqli.cantine.dao;


import fr.sqli.cantine.entity.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFunctionDao extends JpaRepository<FunctionEntity, String> {
    public Optional<FunctionEntity> findByName(String name);
}
