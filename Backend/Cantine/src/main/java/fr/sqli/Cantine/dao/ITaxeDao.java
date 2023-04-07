package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.TaxeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ITaxeDao extends JpaRepository<TaxeEntity,Integer> {
}
