package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDao  extends JpaRepository<AdminEntity, Integer> {

     public Optional <AdminEntity> findByEmail(String email);
}
