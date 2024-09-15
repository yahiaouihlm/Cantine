package fr.sqli.cantine.dao;


import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IConfirmationTokenDao extends JpaRepository<ConfirmationTokenEntity, String> {

    public Optional<ConfirmationTokenEntity> findByToken(String token);


    @Query(value = "SELECT token FROM ConfirmationTokenEntity token JOIN token.admin admin WHERE admin = ?1")
    public Optional<ConfirmationTokenEntity> findByAdmin(UserEntity admin);

    @Query(value = "SELECT token FROM ConfirmationTokenEntity token JOIN token.student student WHERE student = ?1")
    public Optional<ConfirmationTokenEntity> findByStudent(UserEntity student);
}
