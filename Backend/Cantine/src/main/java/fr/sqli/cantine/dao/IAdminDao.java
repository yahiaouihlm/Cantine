package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAdminDao extends JpaRepository<AdminEntity, Integer> {


     @Query(value = "SELECT admin FROM AdminEntity admin ORDER BY admin.id  ASC LIMIT 1")
     public Optional <AdminEntity> findRandomAdmin();
     public Optional <AdminEntity> findByEmail(String email);

     public Optional <AdminEntity> findByUuid(String uuid);
}
