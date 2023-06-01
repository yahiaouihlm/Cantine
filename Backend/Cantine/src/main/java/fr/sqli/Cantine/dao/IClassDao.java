package fr.sqli.Cantine.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClassDao extends JpaRepository<fr.sqli.Cantine.entity.ClassEntity, Integer> {
}
