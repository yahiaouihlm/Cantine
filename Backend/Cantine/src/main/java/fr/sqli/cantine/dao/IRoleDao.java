package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleDao extends JpaRepository<RoleEntity, String> {

    Optional<RoleEntity> findByLabel(String label);
}
