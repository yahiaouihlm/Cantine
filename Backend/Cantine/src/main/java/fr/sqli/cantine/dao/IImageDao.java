package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IImageDao extends JpaRepository<ImageEntity, String> {
}
