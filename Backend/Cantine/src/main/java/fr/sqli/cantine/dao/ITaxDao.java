package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaxDao extends JpaRepository<TaxEntity, Integer> {

}
