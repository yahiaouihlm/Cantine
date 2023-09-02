package fr.sqli.Cantine.dao;


import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.ConfirmationTokenEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IConfirmationTokenDao extends JpaRepository<ConfirmationTokenEntity, Integer> {

    public Optional<ConfirmationTokenEntity>findByToken(String token);

    public Optional<ConfirmationTokenEntity>findByAdmin(AdminEntity admin);

    public  Optional<ConfirmationTokenEntity>findByStudent(StudentEntity student);
}
