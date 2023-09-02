package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAdminDao extends JpaRepository<AdminEntity, Integer> {

     public Optional <AdminEntity> findByEmail(String email);
}
