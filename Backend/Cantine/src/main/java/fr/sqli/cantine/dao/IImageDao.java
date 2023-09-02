package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IImageDao extends JpaRepository<ImageEntity, Integer>  {
}
