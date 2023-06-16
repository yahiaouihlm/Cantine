package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaxDao extends JpaRepository<TaxEntity, Integer> {

}
