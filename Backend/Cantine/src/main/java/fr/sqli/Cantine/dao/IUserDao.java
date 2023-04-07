package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserDao extends JpaRepository <UserEntity, Integer>{
    public Optional<UserEntity> findByEmail (String email);
}
