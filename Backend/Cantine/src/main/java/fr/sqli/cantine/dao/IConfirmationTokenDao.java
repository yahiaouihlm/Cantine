package fr.sqli.cantine.dao;


import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IConfirmationTokenDao extends JpaRepository<ConfirmationTokenEntity, Integer> {

    public Optional<ConfirmationTokenEntity>findByToken(String token);

    public Optional<ConfirmationTokenEntity>findByAdmin(AdminEntity admin);

    public  Optional<ConfirmationTokenEntity>findByStudent(StudentEntity student);
}
