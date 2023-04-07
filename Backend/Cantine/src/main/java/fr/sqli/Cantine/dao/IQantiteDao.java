package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.QuantiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IQantiteDao extends JpaRepository<QuantiteEntity, Integer>  {
}
