package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaxDao extends JpaRepository<TaxEntity, Integer> {

}
