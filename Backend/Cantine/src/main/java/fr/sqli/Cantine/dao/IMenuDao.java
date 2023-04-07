package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMenuDao extends JpaRepository<MenuEntity, Integer> {
}
