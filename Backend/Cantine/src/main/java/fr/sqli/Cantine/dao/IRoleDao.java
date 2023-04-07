package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleDao  extends JpaRepository<RoleEntity, Integer> {
}
