package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.OrderEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderDao  extends JpaRepository<OrderEntity , Integer> {




    public List<OrderEntity> findByStudentOrderByCreationDateDesc(StudentEntity student);
    public Optional<OrderEntity> findByUuid(String uuid);
    public List<OrderEntity> findByCreationDate(LocalDate creationDate);
    public List<OrderEntity>findByStudentUuidAndCreationDate(String studentUuid, LocalDate creationDate);
}
