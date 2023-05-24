package fr.sqli.Cantine.dao;


import fr.sqli.Cantine.entity.ConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IConfirmationTokenDao extends JpaRepository<ConfirmationTokenEntity, Integer> {
}
