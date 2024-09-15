package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserDao extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findUserByEmail(String email);

    @Query(value = "SELECT admin FROM UserEntity  admin   JOIN admin.roles role" +
            " WHERE (role.label='ADMIN') " +
            "ORDER BY admin.id  ASC LIMIT 1")
    public Optional<UserEntity> findRandomAdmin();

    @Query(value = "SELECT admin FROM UserEntity  admin   JOIN admin.roles role" +
            " WHERE (role.label='ADMIN' AND admin.email = ?1)")
    public Optional<UserEntity> findAdminByEmail(String username);

  /*  @Query(value = "SELECT admin FROM UserEntity  admin   JOIN admin.roles role" +
            " WHERE (role.label='ADMIN' AND admin.email = ?1)" )

    public Optional <UserEntity> findByEmail(String user);*/

    /******************************** Student *************************/

    @Query(value = "SELECT admin FROM UserEntity  admin   JOIN admin.roles role" +
            " WHERE (role.label='ADMIN' AND admin.id = ?1) ")
    public Optional<UserEntity> findAdminById(String id);


    @Query(value = "SELECT student FROM UserEntity student JOIN student.roles role WHERE (" +
            "role.label = 'STUDENT' AND " +
            "LOWER(REPLACE(student.firstname, ' ', '')) = LOWER(REPLACE(?1, ' ', ''))" +
            "AND LOWER(REPLACE(student.lastname, ' ', '')) = LOWER(REPLACE(?2, ' ', ''))" +
            "AND  student.birthdate  =  ?3 " +

            ")"

    )
    List<UserEntity> findStudentByFirstnameAndLastnameAndBirthdate(String firstName, String lastName, LocalDate birthdate);


    @Query(value = "SELECT student FROM UserEntity student JOIN student.roles role WHERE (" +
            "role.label = 'STUDENT' AND " +
            "student.email = ?1 " +
            ")"
    )
    Optional<UserEntity> findStudentByEmail(String email);


    @Query(value = "SELECT student FROM UserEntity student JOIN student.roles role WHERE (" +
            "role.label = 'STUDENT' AND " +
            "student.id = ?1 " +
            ")"
    )
    Optional<UserEntity> findStudentById(String id);

}
